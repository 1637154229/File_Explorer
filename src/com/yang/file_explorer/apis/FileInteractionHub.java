package com.yang.file_explorer.apis;

import java.io.File;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.actionbarsherlock.view.ActionMode;
import com.yang.file_explorer.apis.FileListItem.ModeCallback;
import com.yang.file_explorer.R;
import com.yang.file_explorer.entity.FileInfo;
import com.yang.file_explorer.entity.GlobalConsts;
import com.yang.file_explorer.interfaces.IFileInteractionListener;
import com.yang.file_explorer.interfaces.IOperationProgressListener;
import com.yang.file_explorer.ui.MainActivity;
import com.yang.file_explorer.utils.FileUtil;
import com.yang.file_explorer.utils.LogUtils;

public class FileInteractionHub implements IOperationProgressListener {

	private static final String LOG_TAG = "FileInteractionHub";

	private IFileInteractionListener mFileInteractionListener;

	private ArrayList<FileInfo> mCheckedFileNameList = new ArrayList<FileInfo>();

	private ArrayList<FileInfo> mStartFileNameList = new ArrayList<FileInfo>();

	private FileOperationHelper mFileOperationHelper;

	private FileSortHelper mFileSortHelper;
	
	private ProgressDialog progressDialog;

	private Context mContext;

	// File List view setup
	private ListView mFileListView;

	/*
	 * ��ǰ�ļ�·��
	 */
	private String mCurrentPath;

	/*
	 * ��Ŀ¼�ļ�·��
	 */
	private String mRootPath;

	public enum Mode {
		View, Pick
	};

	private Mode mcurrentMode;

	/*
	 * 
	 */
	public FileInteractionHub(IFileInteractionListener fileInteractionListener) {
		assert (fileInteractionListener != null);

		mFileInteractionListener = fileInteractionListener;
		mFileSortHelper = new FileSortHelper();
		mFileOperationHelper = new FileOperationHelper(this);
		mContext = mFileInteractionListener.getContext();
		setup();
	}

	private void setup() {
		setupFileListView();
	}

	/*
	 * listview �¼� 
	 */
	private void setupFileListView() {
		mFileListView = (ListView) mFileInteractionListener
				.getViewById(R.id.file_path_list);

		mFileListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				onListItemClick(parent, view, position, id);
			}
		});
	}
	
	private void showProgress(String msg) {
		progressDialog = new ProgressDialog(mContext);
		// dialog.setIcon(R.drawable.icon);
		progressDialog.setMessage(msg);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);
		progressDialog.show();
	}

	/*
	 * ListView ˢ��
	 */
	public void refreshFileList() {
		clearSelection();

		// onRefreshFileList returns true indicates list has changed
		mFileInteractionListener.onRefreshFileList(mCurrentPath,
				mFileSortHelper);

	}

	/*
	 * listView ����¼�
	 */
	public void onListItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		FileInfo lFileInfo = mFileInteractionListener.getItem(position);

		if (lFileInfo == null) {
			LogUtils.e(LOG_TAG, "file does not exist on position:" + position);
			return;
		}

		if (isInSelection()) {
			boolean selected = lFileInfo.Selected;
			ActionMode actionMode = ((MainActivity) mContext).getActionMode();
			ImageView checkBox = (ImageView) view
					.findViewById(R.id.file_checkbox);
			if (selected) {
				mCheckedFileNameList.remove(lFileInfo);
				checkBox.setImageResource(R.drawable.btn_check_off);
			} else {
				mCheckedFileNameList.add(lFileInfo);
				checkBox.setImageResource(R.drawable.btn_check_on);
			}
			if (actionMode != null) {
				if (mCheckedFileNameList.size() == 0)
					actionMode.finish();
				else
					actionMode.invalidate();
			}
			lFileInfo.Selected = !selected;

			FileUtil.updateActionModeTitle(actionMode, mContext,
					getSelectedFileList().size());
			return;
		}

		if (!lFileInfo.IsDir) {
			if (mcurrentMode == Mode.Pick) {
				mFileInteractionListener.onPick(lFileInfo);
			} else {
				viewFile(lFileInfo);
			}
			return;
		}

		mCurrentPath = getAbsoluteName(mCurrentPath, lFileInfo.fileName);
		ActionMode actionMode = ((MainActivity) mContext).getActionMode();
		if (actionMode != null) {
			actionMode.finish();
		}
		refreshFileList();
	}

	// check or uncheck
	public boolean onCheckItem(FileInfo f, View v) {
		switch (v.getId()) {
		case R.id.file_checkbox_area: {
			if (f.Selected) {
				mCheckedFileNameList.add(f);
			} else {
				mCheckedFileNameList.remove(f);
			}
		}
			break;
		case R.id.favorite_area: {
			if (f.Started) {
				mStartFileNameList.add(f);
			} else {
				mStartFileNameList.remove(f);
			}
		}
			break;
		default:
			break;
		}

		return true;
	}

	/*
	 * ����ļ�
	 */
	private void viewFile(FileInfo lFileInfo) {
		try {
			IntentBuilder.viewFile(mContext, lFileInfo.filePath);
		} catch (ActivityNotFoundException e) {
			LogUtils.e(LOG_TAG, "fail to view file: " + e.toString());
		}
	}

	/*
	 * �Ƿ�ȫ��ѡ��
	 */

	public boolean isAllSelection() {
		return mCheckedFileNameList.size() == mFileInteractionListener
				.getAllFiles().size();
	}

	/*
	 * �Ƿ����ļ�ѡ��״̬
	 */
	public boolean isInSelection() {
		return mCheckedFileNameList.size() > 0;
	}

	/*
	 * ��ȡ�ļ�����·��
	 */
	private String getAbsoluteName(String path, String name) {
		return path.equals(GlobalConsts.ROOT_PATH) ? path + name : path
				+ File.separator + name;
	}

	/*
	 * ѡ���ļ�����
	 */
	public ArrayList<FileInfo> getSelectedFileList() {
		return mCheckedFileNameList;
	}

	public void setRootPath(String path) {
		mRootPath = path;
		mCurrentPath = path;
	}

	public String getRootPath() {
		return mRootPath;
	}

	public String getCurrentPath() {
		return mCurrentPath;
	}

	public void setCurrentPath(String path) {
		mCurrentPath = path;
	}

	public void setMode(Mode mode) {
		mcurrentMode = mode;
	}

	public Mode getMode() {
		return mcurrentMode;
	}

	public FileInfo getItem(int pos) {
		return mFileInteractionListener.getItem(pos);
	}

	@Override
	public void onFinish() {
		// TODO Auto-generated method stub
        if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
        
        mFileInteractionListener.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				clearSelection();
				refreshFileList();
			}
		});
	}

	@Override
	public void onFileChanged(String path) {
		// TODO Auto-generated method stub
        notifyFileSystemChanged(path);
	}
	
	/*
	 * ֪ͨɨ��Sd��
	 */
	private void notifyFileSystemChanged(String path) {
		if (path == null)
			return;
		final File f = new File(path);
		if (f.isDirectory()) {
			MediaScannerConnection.scanFile(mContext,new String[]{path}, null, null);
		} else {
			MediaScanner mediaScanner = new MediaScanner(mContext);
			mediaScanner.scanFile(f, null);
		}
		
	}

	// /////////////////////////////////////////////�ļ���������////////////////////////////////////////////////////////////////////////////
	/*
	 * ȫѡ
	 */
	public void onOperationSelectAll() {
		mCheckedFileNameList.clear();
		for (FileInfo f : mFileInteractionListener.getAllFiles()) {
			f.Selected = true;
			mCheckedFileNameList.add(f);
		}

		MainActivity mainActivity = (MainActivity) mContext;
		ActionMode mode = mainActivity.getActionMode();
		if (mode == null) {
			mode = mainActivity
					.startActionMode(new ModeCallback(mContext, this));
			mainActivity.setActionMode(mode);
			FileUtil.updateActionModeTitle(mode, mContext,
					mCheckedFileNameList.size());
		}

		mFileInteractionListener.onDataChanged();
	}

	/*
	 * ȡ��ѡ�е������б�
	 */

	public void clearSelection() {
		if (mCheckedFileNameList.size() > 0) {
			for (FileInfo f : mCheckedFileNameList) {
				if (f == null) {
					continue;
				}
				f.Selected = false;
			}
			mCheckedFileNameList.clear();
			mFileInteractionListener.onDataChanged();
		}
	}

	/*
	 * ɾ���ļ�
	 */
	public void onOperationDelete() {
		doOperationDelete(getSelectedFileList());
	}

	private void doOperationDelete(final ArrayList<FileInfo> selectedFileList){
		final ArrayList<FileInfo> selectedFiles = new ArrayList<FileInfo>(selectedFileList);
		
		Dialog dialog = new AlertDialog.Builder(mContext)
				.setTitle(mContext.getString(R.string.operation_delete_confirm_message))
				
				 .setPositiveButton(R.string.confirm, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if (mFileOperationHelper.Delete(selectedFiles)) {
							showProgress(mContext.getString(R.string.operation_delete));
						}
						clearSelection();
					}
				})
				
				.setNegativeButton(R.string.cancel, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						 clearSelection();
					}
				}).create();
		
		dialog.show();
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
