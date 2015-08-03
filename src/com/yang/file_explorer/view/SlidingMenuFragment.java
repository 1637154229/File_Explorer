package com.yang.file_explorer.view;

import com.yang.file_explorer.R;
import com.yang.file_explorer.entity.MenuItemType;
import com.yang.file_explorer.ui.MainActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

public class SlidingMenuFragment extends Fragment implements OnClickListener{

	//�豸
	private RelativeLayout device;
	
	//ϲ�������ǣ�
	private RelativeLayout favorite;

	//wifi Ftp
	private RelativeLayout wifi;
	
	//�����ļ�
	private RelativeLayout music;
		
	//��Ƶ�ļ�
	private RelativeLayout video;
		
	//ͼ���ļ�
	private RelativeLayout image;
		
	//�ı��ļ�
	private RelativeLayout document;
		
	//ѹ�����ļ�
	private RelativeLayout zip;
		
	//apk�ļ�
	private RelativeLayout apk;
	
	//ѡ��˵�
	private MenuItemType currentmenuItemType = MenuItemType.MENU_DEVICE;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	View menu = inflater.inflate(R.layout.main_slidingmenu,container, false);
    	device   = (RelativeLayout)menu.findViewById(R.id.device);
    	favorite = (RelativeLayout)menu.findViewById(R.id.favorite);
    	wifi     = (RelativeLayout)menu.findViewById(R.id.wifi);
    	music    = (RelativeLayout)menu.findViewById(R.id.music);
    	image    = (RelativeLayout)menu.findViewById(R.id.image);
    	video    = (RelativeLayout)menu.findViewById(R.id.video);
    	document = (RelativeLayout)menu.findViewById(R.id.document);
    	zip      = (RelativeLayout)menu.findViewById(R.id.zip);
    	apk      = (RelativeLayout)menu.findViewById(R.id.apk);
    	
    	//��������¼�
    	device.setOnClickListener(this);
    	favorite.setOnClickListener(this);
    	wifi.setOnClickListener(this);
    	music.setOnClickListener(this);
    	image.setOnClickListener(this);
    	video.setOnClickListener(this);
    	document.setOnClickListener(this);
    	zip.setOnClickListener(this);
    	apk.setOnClickListener(this);
    
    	
    	return menu;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onActivityCreated(savedInstanceState);
    }
    
    public boolean SelMenu(MenuItemType menuType){
    	device.setBackgroundResource(R.drawable.menu_item_selecter);
		device.getChildAt(0).setVisibility(View.GONE);
		
		favorite.setBackgroundResource(R.drawable.menu_item_selecter);
		favorite.getChildAt(0).setVisibility(View.GONE);
		
		wifi.setBackgroundResource(R.drawable.menu_item_selecter);
		wifi.getChildAt(0).setVisibility(View.GONE);
		
		image.setBackgroundResource(R.drawable.menu_item_selecter);
		image.getChildAt(0).setVisibility(View.GONE);
		
		music.setBackgroundResource(R.drawable.menu_item_selecter);
		music.getChildAt(0).setVisibility(View.GONE);
		
		video.setBackgroundResource(R.drawable.menu_item_selecter);
		video.getChildAt(0).setVisibility(View.GONE);
		
		document.setBackgroundResource(R.drawable.menu_item_selecter);
		document.getChildAt(0).setVisibility(View.GONE);
		
		zip.setBackgroundResource(R.drawable.menu_item_selecter);
		zip.getChildAt(0).setVisibility(View.GONE);
		
		apk.setBackgroundResource(R.drawable.menu_item_selecter);
		apk.getChildAt(0).setVisibility(View.GONE);
		
		switch (menuType) {
		case MENU_DEVICE:
			device.setBackgroundResource(R.drawable.menu_selected_tile_bg);
			device.getChildAt(0).setVisibility(View.VISIBLE);
			break;
			
		case MENU_FAVORITE:
			favorite.setBackgroundResource(R.drawable.menu_selected_tile_bg);
			favorite.getChildAt(0).setVisibility(View.VISIBLE);
			break;
			
		case MENU_WIFI:
			wifi.setBackgroundResource(R.drawable.menu_selected_tile_bg);
			wifi.getChildAt(0).setVisibility(View.VISIBLE);
			break;
			
		case MENU_VIDEO:
			video.setBackgroundResource(R.drawable.menu_selected_tile_bg);
			video.getChildAt(0).setVisibility(View.VISIBLE);
			break;
			
		case MENU_MUSIC:
			music.setBackgroundResource(R.drawable.menu_selected_tile_bg);
			music.getChildAt(0).setVisibility(View.VISIBLE);
			break;
			
			
		case MENU_DOCUMENT:
		    document.setBackgroundResource(R.drawable.menu_selected_tile_bg);
			document.getChildAt(0).setVisibility(View.VISIBLE);
			break;
			
		case MENU_ZIP:
			zip.setBackgroundResource(R.drawable.menu_selected_tile_bg);
			zip.getChildAt(0).setVisibility(View.VISIBLE);
			break;
			
		case MENU_APK:
			apk.setBackgroundResource(R.drawable.menu_selected_tile_bg);
			apk.getChildAt(0).setVisibility(View.VISIBLE);
			break;
			
		case MENU_IMAGE:
			image.setBackgroundResource(R.drawable.menu_selected_tile_bg);
			image.getChildAt(0).setVisibility(View.VISIBLE);
			break;
			
		default:
			break;
		}
		
		currentmenuItemType = menuType;
		return true;
    }
    
    
    private void OpenFragment(MenuItemType menuType){
    	if(getActivity() != null && getActivity() instanceof MainActivity){
    		((MainActivity)getActivity()).setShowSelFragments(menuType);
    	}
    	
    	return;
    }
    
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.device:
			OpenFragment(MenuItemType.MENU_DEVICE);
			break;
			
		case R.id.favorite:
			OpenFragment(MenuItemType.MENU_FAVORITE);
			break;
			
		case R.id.wifi:
			OpenFragment(MenuItemType.MENU_WIFI);
			break;
			
		case R.id.video:
			OpenFragment(MenuItemType.MENU_VIDEO);
			break;
			
		case R.id.music:
			OpenFragment(MenuItemType.MENU_MUSIC);
			break;
			
			
		case R.id.document:
			OpenFragment(MenuItemType.MENU_DOCUMENT);
			break;
			
		case R.id.zip:
			OpenFragment(MenuItemType.MENU_ZIP);
			break;
			
		case R.id.apk:
			OpenFragment(MenuItemType.MENU_APK);
			break;
			
		case R.id.image:
			OpenFragment(MenuItemType.MENU_IMAGE);
			break;
			
		default:
			break;
		}
	}
	

	
}
