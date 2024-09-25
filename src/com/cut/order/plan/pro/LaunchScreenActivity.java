package com.cut.order.plan.pro;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazon.device.ads.AdLayout;
import com.amazon.device.ads.AdRegistration;
import com.cut.order.plan.pro.data.StyleData;

public class LaunchScreenActivity extends Activity implements OnClickListener {

	public MyAdapter adapter;
	private AdLayout adView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.launch_page);
	    
	    AdRegistration.setAppKey(getResources().getString(R.string.appKeyAmazon));
		this.adView = (AdLayout) findViewById(R.id.adview);
        this.adView.loadAd();
	    
	    adapter = new MyAdapter();
	    ListView existingStyleList = (ListView) findViewById(R.id.style_view);
	    existingStyleList.setAdapter(adapter);
        existingStyleList.setOnItemClickListener(adapter);
        
        findViewById(R.id.new_project).setOnClickListener(this);
	}
	
	@Override
    protected void onResume() {
    	super.onResume();
    	adapter.clear();
    	ArrayList<StyleData> check = new ArrayList<StyleData>(MainApp.getDB().fetchStyles());
    	int a = check.size();
    	for(int i = 0; i < a; i++){
    		adapter.add(check.get(i));
    	}
    	
    }
	
	@Override
	public void onClick(View arg0) {
		final EditText input = new EditText(this);
		new AlertDialog.Builder(this)
		.setTitle("New Style Name")
		.setView(input)
		.setPositiveButton("Create", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String styleName = input.getText().toString();
				if (styleName.equals("")) {
					Toast.makeText(getApplicationContext(), "Name cannot be empty.", Toast.LENGTH_SHORT).show();
					return;
				}
				Pattern p = Pattern.compile("[^a-z0-9]", Pattern.CASE_INSENSITIVE);
				Matcher m = p.matcher(styleName);
				boolean b = m.find();
				if(b){
					Toast.makeText(getApplicationContext(), "Invalid Characters.", Toast.LENGTH_SHORT).show();
					return;
				}
				if ( Character.isDigit(styleName.charAt(0)) )
				{
					Toast.makeText(getApplicationContext(), "Name should start with an alphabet.", Toast.LENGTH_SHORT).show();
				    return;
				}
				StyleData check = new StyleData();
				check = MainApp.getDB().getStyleData(styleName);
				
				
				if(check.styleName == null){
					MainApp.getDB().newStyle(styleName);
					showStyle(styleName);						
					return;
				}else{
					Toast.makeText(getApplicationContext(), "Style Name already exists.", Toast.LENGTH_SHORT).show();
				}
			}
		}).create().show();
		
	}
	
	private void showStyle(String styleName) {
		Intent intent = new Intent(this, StyleDetailActivity.class);
		intent.putExtra("styleName", styleName);
		startActivity(intent);
	}
	
	private class MyAdapter extends ArrayAdapter<StyleData> implements OnItemClickListener{
		private final LayoutInflater inflater;
		
		public MyAdapter() {
			super(LaunchScreenActivity.this, R.id.t1, R.layout.style_entry);
			inflater = LayoutInflater.from(LaunchScreenActivity.this);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder;
			if (convertView == null){
				convertView = inflater.inflate(R.layout.style_entry, null);
				holder = new Holder();
				holder.t1 = (TextView) convertView.findViewById(R.id.t1);
				holder.t2 = (TextView) convertView.findViewById(R.id.t2);
				convertView.setTag(holder);
	//			convertView.setOnClickListener(holder);
			}else {
				holder = (Holder)convertView.getTag();
			}

			StyleData data = getItem(position);
			holder.t1.setText(data.styleName);
			holder.t2.setText(data.edit_date);

			return convertView;
			
		}

		@Override
		public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
			showStyle(getItem(position).styleName);
			
		}
		
	}
	
	private class Holder {
    	TextView t1, t2;
    }
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    	case R.id.menu_about:
	    		Intent intent = new Intent(this, AboutActivity.class);
	    		startActivity(intent);
	    		return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    
	    }   
	}
}
