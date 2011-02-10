package org.instk.logger;

import java.util.List;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class SensorInfo extends Activity {
	
	List<Sensor> sList=null;
	TextView vmname;
	TextView vmmaxrange;
	TextView vmmindelay;
	TextView vmpower;
	TextView vmresolution;
	TextView vmtype;
	TextView vmvendor;
	TextView vmversion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Get a reference for the sensor manager
		SensorManager sMan=(SensorManager) getSystemService(SENSOR_SERVICE);
		sList=sMan.getSensorList(Sensor.TYPE_ALL);
		
		setContentView(R.layout.sinfo);
		Spinner mSpinner=(Spinner) findViewById(R.id.spinner);
		
		Bundle extras = getIntent().getExtras();
		String[] snames=extras.getStringArray("snames");
		int nsen=extras.getInt("nsen");
		
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item);
		for (int i=0;i<nsen;i++)
			adapter.add(snames[i]);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		mSpinner.setAdapter(adapter);
		
		mSpinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
		
		vmname=(TextView) findViewById(R.id.siname);
		vmmaxrange=(TextView) findViewById(R.id.simaxrange);
		vmmindelay=(TextView) findViewById(R.id.simindelay);
		vmpower=(TextView) findViewById(R.id.sipower);
		vmresolution=(TextView) findViewById(R.id.siresolution);
		vmtype=(TextView) findViewById(R.id.sitype);
		vmvendor=(TextView) findViewById(R.id.sivendor);
		vmversion=(TextView) findViewById(R.id.siversion);
		
		vmname.setText(sList.get(0).getName());
		vmmaxrange.setText(Float.toString(sList.get(0).getMaximumRange()));
		vmmindelay.setText("Requires Level 9");
		vmpower.setText(Float.toString(sList.get(0).getPower()));
		vmresolution.setText(Float.toString(sList.get(0).getResolution()));
		vmtype.setText(Integer.toString(sList.get(0).getType()));
		vmvendor.setText(sList.get(0).getVendor());
		vmversion.setText(Integer.toString(sList.get(0).getVersion()));
	}
	
	public class MyOnItemSelectedListener implements OnItemSelectedListener {

	    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
	      
	    	vmname.setText(sList.get(pos).getName());
			vmmaxrange.setText(Float.toString(sList.get(pos).getMaximumRange()));
			vmmindelay.setText("Requires Level 9");
			vmpower.setText(Float.toString(sList.get(pos).getPower()));
			vmresolution.setText(Float.toString(sList.get(pos).getResolution()));
			vmtype.setText(Integer.toString(sList.get(pos).getType()));
			vmvendor.setText(sList.get(pos).getVendor());
			vmversion.setText(Integer.toString(sList.get(pos).getVersion()));
	    }

	    public void onNothingSelected(AdapterView parent) {
	      // Do nothing.
	    }
	}
}
