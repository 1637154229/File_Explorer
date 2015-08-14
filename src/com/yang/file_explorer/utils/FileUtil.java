package com.yang.file_explorer.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashSet;

import com.actionbarsherlock.view.ActionMode;
import com.yang.file_explorer.R;
import com.yang.file_explorer.entity.FileInfo;
import com.yang.file_explorer.entity.Settings;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FileUtil {

	private static final String LOG_TAG = "Util";

	private static String ANDROID_SECURE = "/mnt/sdcard/.android_secure";

	public static HashSet<String> sDocMimeTypesSet = new HashSet<String>() {
		{
			add("text/plain");
			add("text/plain");
			add("application/pdf");
			add("application/msword");
			add("application/vnd.ms-excel");
			add("application/vnd.ms-excel");
		}
	};

	/*
	 * ��ȡ��洢SD��·��
	 */
	public static String getSdDirectory() {
		return Environment.getExternalStorageDirectory().getPath();
	}

	/*
	 * Sd��״̬ true SD����������
	 */
	public static boolean isSDCardReady() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	public static boolean setText(View view, int id, String text) {
		TextView textView = (TextView) view.findViewById(id);
		if (textView == null)
			return false;

		textView.setText(text);
		return true;
	}

	/*
	 * �Ƿ񳣼��ļ�
	 */
	public static boolean isNormalFile(String fullName) {
		return !fullName.equals(ANDROID_SECURE);
	}

	private static String[] SysFileDirs = new String[] { "miren_browser/imagecaches" };

	/*
	 * �Ƿ����ʾ�ļ�
	 */
	public static boolean shouldShowFile(String path) {
		return shouldShowFile(new File(path));
	}

	public static boolean shouldShowFile(File file) {
		boolean show = Settings.instance().getShowDotAndHiddenFiles();
		if (show)
			return true;

		if (file.isHidden())
			return false;

		if (file.getName().startsWith("."))
			return false;

		String sdFolder = getSdDirectory();
		for (String s : SysFileDirs) {
			if (file.getPath().startsWith(makePath(sdFolder, s)))
				return false;
		}

		return true;
	}

	public static String makePath(String path1, String path2) {
		if (path1.endsWith(File.separator))
			return path1 + path2;

		return path1 + File.separator + path2;
	}

	/*
	 * ʱ���ʽ��
	 */
	public static String formatDateString(Context context, long time) {
		DateFormat dateFormat = android.text.format.DateFormat
				.getDateFormat(context);
		DateFormat timeFormat = android.text.format.DateFormat
				.getTimeFormat(context);
		Date date = new Date(time);

		return dateFormat.format(date) + " " + timeFormat.format(date);
	}

	/*
	 * �����ļ���С storage, G M K B
	 */
	public static String convertStorage(long size) {
		long kb = 1024;
		long mb = kb * 1024;
		long gb = mb * 1024;

		if (size >= gb) {
			return String.format("%.1f GB", (float) size / gb);
		} else if (size >= mb) {
			float f = (float) size / mb;
			return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
		} else if (size >= kb) {
			float f = (float) size / kb;
			return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
		} else
			return String.format("%d B", size);
	}

	/*
	 * ��ȡ�ļ����ͣ���׺����
	 */
	public static String getExtFromFilename(String filename) {
		int dotPosition = filename.lastIndexOf('.');
		if (dotPosition != -1) {
			return filename.substring(dotPosition + 1, filename.length());
		}
		return "";
	}

	/*
	 * ��ȡ�ļ���
	 */
	public static String getNameFromFilename(String filename) {
		int dotPosition = filename.lastIndexOf('.');
		if (dotPosition != -1) {
			return filename.substring(0, dotPosition);
		}
		return "";
	}

	/*
	 * �������µİ취��ȡAPKͼ�֮꣬ǰ��ʧ������Ϊandroid�д��ڵ�һ��BUG,ͨ�� appInfo.publicSourceDir =
	 * apkPath;������������⣬����μ�:
	 * http://code.google.com/p/android/issues/detail?id=9151
	 */
	public static Drawable getApkIcon(Context context, String apkPath) {
		PackageManager pm = context.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(apkPath,
				PackageManager.GET_ACTIVITIES);
		if (info != null) {
			ApplicationInfo appInfo = info.applicationInfo;
			appInfo.sourceDir = apkPath;
			appInfo.publicSourceDir = apkPath;
			try {
				return appInfo.loadIcon(pm);
			} catch (OutOfMemoryError e) {
				LogUtils.e(LOG_TAG, e.toString());
			}
		}
		return null;
	}

	/*
	 * ��ȡ�ļ���Ϣ
	 */
	public static FileInfo GetFileInfo(File f, FilenameFilter filter,
			boolean showHidden) {
		FileInfo lFileInfo = new FileInfo();
		String filePath = f.getPath();
		File lFile = new File(filePath);
		lFileInfo.canRead = lFile.canRead();
		lFileInfo.canWrite = lFile.canWrite();
		lFileInfo.isHidden = lFile.isHidden();
		lFileInfo.fileName = f.getName();
		lFileInfo.ModifiedDate = lFile.lastModified();
		lFileInfo.IsDir = lFile.isDirectory();
		lFileInfo.filePath = filePath;
		if (lFileInfo.IsDir) {
			int lCount = 0;
			File[] files = lFile.listFiles(filter);

			// null means we cannot access this dir
			if (files == null) {
				return null;
			}

			for (File child : files) {
				if ((!child.isHidden() || showHidden)
						&& FileUtil.isNormalFile(child.getAbsolutePath())) {
					lCount++;
				}
			}
			lFileInfo.Count = lCount;

		} else {

			lFileInfo.fileSize = lFile.length();

		}
		return lFileInfo;
	}

	/*
	 * ActionMode ��ʾѡ�еĸ���
	 */
	public static void updateActionModeTitle(ActionMode mode,Button btntitle, Context context, int selectedNum) {
        if (mode != null) {
        	btntitle.setText(context.getString(R.string.multi_select_title,selectedNum));
            if(selectedNum == 0){
                mode.finish();
            }
        }
    }
}
