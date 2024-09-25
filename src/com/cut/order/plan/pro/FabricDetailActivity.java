package com.cut.order.plan.pro;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.amazon.device.ads.AdLayout;
import com.amazon.device.ads.AdRegistration;
import com.cut.order.plan.pro.data.FabricData;
import com.cut.order.plan.pro.data.SizeData;
import com.cut.order.plan.pro.dialog.SizeDataDialog;

public class FabricDetailActivity extends Activity {

	SeekBar contigency_select;
	TextView total_val;
	
	public SizeDataAdaptor adaptor;
	public FabricData fabricData;
	public String style_Name;
	public String[] fabNum = new String[]{"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
	private AdLayout adView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.fabric_detail);
	    
	    AdRegistration.setAppKey(getResources().getString(R.string.appKeyAmazon));
		this.adView = (AdLayout) findViewById(R.id.adview);
        this.adView.loadAd();
	    
	    String[] styleName = getIntent().getExtras().getStringArray("styleName");
	    int posi = Integer.parseInt(styleName[2]);
		setTitle(styleName[0] + " >> (" + fabNum[posi] + ") " + styleName[1]);
		this.style_Name = styleName[0];
		fabricData = MainApp.getDB().getFabricData(styleName[0], styleName[1]);
		
		final TextView contigency_val = (TextView)findViewById(R.id.contigency_val); 
		contigency_select = (SeekBar)findViewById(R.id.contigency_select);
		contigency_select.setProgress(fabricData.contigency);
		contigency_val.setText(fabricData.contigency + " %");
		contigency_select.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				contigency_val.setText(arg1 + " %");

			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int contigency = seekBar.getProgress();
				for (int i = adaptor.getCount() - 1; i >=0; i--) {
					adaptor.getItem(i).setContigency(contigency);
				}
				adaptor.notifyDataSetChanged();
				fabricData.contigency = contigency;
				setTotal();
				MainApp.getDB().updateFabricData(fabricData, style_Name);
			}
		});
		
		adaptor = new SizeDataAdaptor();
		ListView size_data_list = (ListView)findViewById(R.id.size_view);
		size_data_list.setAdapter(adaptor);
		size_data_list.setOnItemClickListener(adaptor);
		int x = fabricData.sizes.size();
		for(int i = 0; i < x; i++){
			adaptor.add(fabricData.sizes.get(i));
		}
		
		Button add_size = (Button)findViewById(R.id.add_size);
		add_size.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SizeData size = new SizeData();
				size.setContigency(contigency_select.getProgress());
				SizeDataDialog ae_size = new SizeDataDialog(size, FabricDetailActivity.this, false);
				ae_size.show();

			}
		});
		
		
		total_val = (TextView)findViewById(R.id.total_val);
		setTotal();
	    
	}
	
	public void setTotal(){
		int tottal_cuttable = 0;
		for (int i = adaptor.getCount() - 1; i >=0; i--) {
			tottal_cuttable += adaptor.getItem(i).getCuttable();
		}
		total_val.setText(tottal_cuttable + " Pieces");
	}
	
	public class SizeDataAdaptor extends ArrayAdapter<SizeData> implements OnItemClickListener{
		
		private final LayoutInflater inflater;

		public SizeDataAdaptor() {
			super(FabricDetailActivity.this, R.layout.size_list_data, R.id.size_val);
			inflater = LayoutInflater.from(FabricDetailActivity.this);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder;
			if (convertView == null){
				convertView = inflater.inflate(R.layout.size_list_data, null);
				holder = new Holder();
				holder.size_val = (TextView)convertView.findViewById(R.id.size_val);
				holder.quantity_val = (TextView)convertView.findViewById(R.id.quantity_value);
				holder.contigency_val = (TextView)convertView.findViewById(R.id.contigency_value);
				holder.end_bit_val = (TextView)convertView.findViewById(R.id.end_bit_value);
				convertView.setTag(holder);
			}else {
				holder = (Holder)convertView.getTag();
			}
			SizeData data = getItem(position);
			holder.size_val.setText(data.sizeName);
			holder.quantity_val.setText(data.quantity + " Pieces");
			holder.contigency_val.setText("(+"+data.getContigency()+")");
			holder.end_bit_val.setText("(-"+data.endBit+")");
			return convertView;
		}

		@Override
		public void onItemClick(AdapterView<?> adapter, View view, int position, long arg33) {
			
			SizeDataDialog dialog = new SizeDataDialog(getItem(position), FabricDetailActivity.this, true);
			dialog.show();
			
		}
		
		
	}
	
	private class Holder {
		TextView size_val;
		TextView quantity_val;
		TextView contigency_val;
		TextView end_bit_val;
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fabric_menu, menu);
        return true;
    }
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    	case R.id.menu_about:
	    		Intent intent = new Intent(this, AboutActivity.class);
	    		startActivity(intent);
	    		return true;
	    	case R.id.menu_rename_fabric:
	    		
	    		final EditText input = new EditText(this);
	        	new AlertDialog.Builder(this)
				.setTitle("New Fabric Name")
				.setView(input)
				.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String newName = input.getText().toString();
						if (newName.equals("")) {
							Toast.makeText(getApplicationContext(), "Name cannot be empty.", Toast.LENGTH_SHORT).show();
							return;
						}
						Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
						Matcher m = p.matcher(newName);
						boolean b = m.find();
						if(b){
							Toast.makeText(getApplicationContext(), "Invalid Characters.", Toast.LENGTH_SHORT).show();
							return;
						}
						if ( Character.isDigit(newName.charAt(0)) )
						{
							Toast.makeText(getApplicationContext(), "Name should start with an alphabet.", Toast.LENGTH_SHORT).show();
						    return;
						}
						
						FabricData check = new FabricData();
						check = MainApp.getDB().getFabricData(style_Name, newName);
						
						
						if(check.fabricName == null){
							
							MainApp.getDB().renameFabricData(style_Name, fabricData.fabricName, newName);
							fabricData.fabricName = newName;
							setTitle(style_Name + " >> " + newName);					
							//return;
						}else{
							Toast.makeText(getApplicationContext(), "Fabric Name already exists.", Toast.LENGTH_SHORT).show();
						}
						
						
					}
				}).create().show();
	    		
	    		return true;
	    	case R.id.menu_delete_fabric:
	    		
	    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        	builder.setMessage("Are you sure you want to delete fabric?")
	        			.setTitle("Confirm")
	        	       .setCancelable(false)
	        	       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	        	           public void onClick(DialogInterface dialog, int id) {
	        	        	   MainApp.getDB().deleteFabricData(style_Name, fabricData.fabricName);
	        		           finish();
	        		           return;
	        	           }
	        	       })
	        	       .setNegativeButton("No", new DialogInterface.OnClickListener() {
	        	           public void onClick(DialogInterface dialog, int id) {
	        	                dialog.cancel();
	        	                return;
	        	           }
	        	       });
	        	AlertDialog alert = builder.create();
	        	alert.show();
	    		
	    		return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    
	    }
	    
	}

}
