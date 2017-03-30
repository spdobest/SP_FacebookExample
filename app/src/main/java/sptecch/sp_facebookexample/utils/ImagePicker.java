package sptecch.sp_facebookexample.utils;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

public class ImagePicker extends Activity
{
    private Uri mImageCaptureUri;
	public static final int PICK_FROM_CAMERA = 1;
	public static final int PICK_FROM_GALLERY = 2;
	public static final int IMAGE_PICKER = 111111;
	static final int CROP_IMAGE = 3;
	private Uri mSelectedImageUri;
	int mSize = 300;

	int mPickFrom = PICK_FROM_GALLERY;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		if (getIntent().hasExtra("pick_from"))
		{
			mPickFrom = getIntent().getIntExtra("pick_from", PICK_FROM_GALLERY);
		}
		if (getIntent().hasExtra("size"))
		{
			mSize = getIntent().getIntExtra("size", 300);
		}

		mSelectedImageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"img"+ System.currentTimeMillis()+"tmp_avatar.jpg"));
		if (mPickFrom == PICK_FROM_CAMERA)
			startCamera();
		else
			startGallery();
		TextView view = new TextView(this);
		view.setBackgroundColor(Color.TRANSPARENT);
		setContentView(view);
		super.onCreate(savedInstanceState);
	}

	void startCamera()
	{
		mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "tmp_avatar1.jpg"));
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
		startActivityForResult(intent, PICK_FROM_CAMERA);
	}

	void startGallery()
	{
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_PICK);
		startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_GALLERY);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode != RESULT_OK)
		{
			finish();
			return;
		}
		if (requestCode == PICK_FROM_CAMERA)
		{
			doCrop();
		}
		else if (requestCode == PICK_FROM_GALLERY)
		{
			mImageCaptureUri = data.getData();
			doCrop();
		}
		else if (requestCode == CROP_IMAGE)
		{
			if (data == null)
				data = new Intent();
			data.putExtra("data_uri", mSelectedImageUri.getPath());
			setResult(resultCode, data);
			finish();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void doCrop()
	{
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setType("image/*");
		List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
		int size = list.size();
		if (size == 0)
		{
			Toast.makeText(this, "Can not find image crop application installed", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		else
		{
			intent.setData(mImageCaptureUri);
			intent.putExtra("outputX", mSize);
			intent.putExtra("outputY", mSize);
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("scale", true);
			// intent.putExtra("return-data", true);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, mSelectedImageUri);
			Intent i = new Intent(intent);
			ResolveInfo res = list.get(0);
			i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
			startActivityForResult(i, CROP_IMAGE);
		}
	}

}