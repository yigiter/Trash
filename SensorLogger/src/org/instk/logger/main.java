package org.instk.logger;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;


public class main extends Activity implements OnClickListener, SensorEventListener{
	private final int MAXS=20;
	
	private SensorManager sMan=null;
	private  List<Sensor> sList;
	private String[] sName=new String[MAXS];
	private int[][] sInfo=new int[MAXS][2];
	private TextView dbgvw;
	private Button btnstr;
	private Button btnev;
	private Button btnstp;
	
	private int rate=SensorManager.SENSOR_DELAY_UI;
	
	private int nsen;
	private int[] act_sen=new int[MAXS];
	
	
	private DataOutputStream[] fout=new DataOutputStream[MAXS];
	
	private SimpleDateFormat dtf= new SimpleDateFormat("dd.HH.mm.ss");
	
	private int mode_mon=0, mode_log=0; //0:non-active	
	
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Get a reference to manager
        sMan=(SensorManager) getSystemService(SENSOR_SERVICE);
        //Get the list of sensors
        sList=sMan.getSensorList(Sensor.TYPE_ALL);
        nsen=sList.size();
        assignnames();
        
        //Adjust the initial view
        setContentView(R.layout.main);
        dbgvw=(TextView)findViewById(R.id.dbgvw);
        dbgvw.setMovementMethod(new ScrollingMovementMethod());
        dbgvw.setScrollbarFadingEnabled(false);
        
        btnstr=(Button) findViewById(R.id.btnstr);
        btnev=(Button) findViewById(R.id.btnev);
        btnstp=(Button) findViewById(R.id.btnstp);
        btnstr.setOnClickListener(this);
        btnev.setOnClickListener(this);
        btnstp.setOnClickListener(this);
        btnstp.setEnabled(false);
        
        ToggleButton tgbtnmon=(ToggleButton) findViewById(R.id.tgbtnmon);
        tgbtnmon.setChecked(false);
        tgbtnmon.setOnClickListener(this);
        
        ToggleButton tgbtnlog=(ToggleButton) findViewById(R.id.tgbtnlog);
        tgbtnlog.setChecked(false);
        tgbtnlog.setOnClickListener(this);
        
        //Read (&set) the preferences
        set_prefs(act_sen, rate);
    }
    
    public void onClick(View v) {
    	int typ=v.getId();
    	if (typ==R.id.btnstr) {
    		//Register enabled sensors
    		register_sensors(act_sen, rate);
    		
    		//Warn the user if no consumer specified
    		if (mode_mon==0 && mode_log==0) {
    			debugview("Sensors are activated. But they are neither monitored nor logged.");
    		}
    		
    		btnstr.setEnabled(false);
    		btnstp.setEnabled(true);
    	}
    	if (typ==R.id.btnstp) {
    		if (mode_mon==0 && mode_log==0) {
    			sMan.unregisterListener(this);
    			btnstr.setEnabled(true);
    			btnstp.setEnabled(false);
    		}
    		else
    		{
    			debugview("Please toggle off all clients first.");
    		}
    	}
    	if (typ==R.id.btnev) {
    		dbgvw.setText("Ext. Event\n" + dbgvw.getText());
    	}
    	if (typ==R.id.tgbtnmon) {
    		if (((CompoundButton) v).isChecked()) {
    			
    			mode_mon=1;
    			debugview("Monitor is On");
    			
    			TextView hd = null;
    			int mord=0;
    			
    			for (int i=0;i<nsen;i++) {
    				if (act_sen[i]==1) {
    					mord++;
    					
    					switch (mord) {
    	    			case 1:
    	    				hd=(TextView)findViewById(R.id.mh1);
    	    				break;
    	    			case 2:
    	    				hd=(TextView)findViewById(R.id.mh2);
    	    				break;
    	    			case 3:
    	    				hd=(TextView)findViewById(R.id.mh3);
    	    				break;
    	    			case 4:
    	    				hd=(TextView)findViewById(R.id.mh4);
    	    				break;
    	    			case 5:
    	    				hd=(TextView)findViewById(R.id.mh5);
    	    				break;
    	    			case 6:
    	    				hd=(TextView)findViewById(R.id.mh6);
    	    				break;
    	    			default:
    	    				debugview("Maximum 6 sensor can be monitored at a time.");
    	    				break;
    	    			}
    					hd.setText(sName[i]);
    				}
    			}	
    		}
    		else {
    			mode_mon=0;
    			debugview("Monitor is Off");
    		}
    		
    	}
    	if (typ==R.id.tgbtnlog) {
    		if (((ToggleButton) v).isChecked()) {
    			if (openlogfiles()==1) {
    				mode_log=1;
    				debugview("Log files are open.");
    			}
    			else {
    				((ToggleButton) v).setChecked(false);
    				debugview("Error in opening log files!");
    			}
    			
    		}
    		else {
    			mode_log=0;
    			debugview("Log files are closed.");
    		}
    	}
    }
    
    private int openlogfiles() {
    
		String ftag=dtf.format(new Date());
		File dbase=new File(Environment.getExternalStorageDirectory(),ftag);
		String fname;
		
		//Open the files and register for the listeners
		for (int i=0;i<nsen;i++) {
			if (act_sen[i]==1) {
				fname=dbase.toString()+sName[i]+".bin";
				
				try {
					fout[i]=new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fname)));
				} catch (FileNotFoundException e) {
					//Do something
					debugview(fname + "Could not be opened. You may not have perfmission to write external storage.");
					closelogfiles();
					return 0;
				}
				debugview(fname+ " is opened.");
			}
		}
		//All files are successfully opened
		return 1;
    }
    
    private void closelogfiles() {		
		//Close the files
		for (int i=0;i<nsen;i++) {
			if (fout[i]!=null && act_sen[i]==1) {
				if (act_sen[i]==1) {
					try {
						fout[i].close();
						fout[i]=null;
					} catch (IOException e) {
						//Do something
						debugview("Some open files could not be closed.Dunno why.");
					}
				}
			else if (fout[i]!=null) {
				debugview("We should not be here. File is open but the corresponding sensor is not active.");
			}	
			}
		}
    }
    
    private void debugview(String arg) {
    	dbgvw.setText(arg+"\n" + dbgvw.getText());
    }
    
    private void assignnames() {
    	for (int i=0;i<nsen;i++) {
    		switch (sList.get(i).getType()) {
    		case Sensor.TYPE_ACCELEROMETER:
    			sName[i]=new String("accelerometer");
    			sInfo[i][0]=Sensor.TYPE_ACCELEROMETER;
    			sInfo[i][1]=3;
    			break;
    		case Sensor.TYPE_GYROSCOPE:
    			sName[i]=new String("gyroscope");
    			sInfo[i][0]=Sensor.TYPE_GYROSCOPE;
    			sInfo[i][1]=3;
    			break;
    		case Sensor.TYPE_LIGHT:
    			sName[i]=new String("light");
    			sInfo[i][0]=Sensor.TYPE_LIGHT;
    			sInfo[i][1]=1;
    			break;
    		case Sensor.TYPE_MAGNETIC_FIELD:
    			sName[i]=new String("magnetic_field");
    			sInfo[i][0]=Sensor.TYPE_MAGNETIC_FIELD;
    			sInfo[i][1]=3;
    			break;
    		case Sensor.TYPE_ORIENTATION:
    			sName[i]=new String("orientation");
    			sInfo[i][0]=Sensor.TYPE_ORIENTATION;
    			sInfo[i][1]=3;
    			break;
    		case Sensor.TYPE_PRESSURE:
    			sName[i]=new String("pressure");
    			sInfo[i][0]=Sensor.TYPE_PRESSURE;
    			sInfo[i][1]=1;
    			break;
    		case Sensor.TYPE_PROXIMITY:
    			sName[i]=new String("proximity");
    			sInfo[i][0]=Sensor.TYPE_PROXIMITY;
    			sInfo[i][1]=3;
    			break;
    		case Sensor.TYPE_TEMPERATURE:
    			sName[i]=new String("temperature");
    			sInfo[i][0]=Sensor.TYPE_TEMPERATURE;
    			sInfo[i][1]=1;
    			break;
    		default:	//Otherwise use the same name framework use
    			sName[i]=sList.get(i).getName();
    			sInfo[i][0]=sList.get(i).getType();
    			sInfo[i][1]=0;	//Length of data unknown
    		}
    	}
    }

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	public void onSensorChanged(SensorEvent event) {
		//Determine the index for the corresponding type
		int typ=event.sensor.getType();
		int ind=MAXS;
		int mord=0;
		int i;
		for (i=0;i<nsen;i++) {
			if (act_sen[i]==1) mord++;
			if (typ==sInfo[i][0]) {
				ind=i;
				break;
			}
		}
		
		if (ind==MAXS) {
			debugview("An undefined event occoured.");
			return;
		}
		
		//We are going to log the data
		if (mode_log==1) {	
			if (fout[ind]==null) {
				debugview("WTF::Log file is not open.");
			}
			else {
				
				try {
					fout[ind].writeLong(event.timestamp);
					if (sInfo[ind][1]!=0) {
						for (i=0;i<sInfo[ind][1];i++) {
							fout[ind].writeFloat(event.values[i]);
						}
					}
					else {
						for (i=0;i<event.values.length;i++) {
							fout[ind].writeFloat(event.values[i]);
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					debugview("Error writing file.");
				}
			}
		}
		
		//We are going to monitor the data
		if (mode_mon==1) {
			TextView dt;
			switch (mord) {
			case 1:
				dt=(TextView)findViewById(R.id.md1);
				break;
			case 2:
				dt=(TextView)findViewById(R.id.md2);
				break;
			case 3:
				dt=(TextView)findViewById(R.id.md3);
				break;
			case 4:
				dt=(TextView)findViewById(R.id.md4);
				break;
			case 5:
				dt=(TextView)findViewById(R.id.md5);
				break;
			case 6:
				dt=(TextView)findViewById(R.id.md6);
				break;
			default:
				return;
			}
			dt.setText(data2text(event.values));		
		}
	}
	
	private String data2text(float[] values) {
		String txt=Float.toString(values[0]);
		for (int i=1;i<values.length;i++) {
			txt=txt+"\t#\t";
			txt=txt+Float.toString(values[i]);
		}
		return txt;
	}
	
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {      
        // Inflate the currently selected menu XML resource.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logmenu, menu);
        return true;
    }
	
	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mninfo:
            {
                Intent mIntent=new Intent(this, SensorInfo.class);
                mIntent.putExtra("snames", sName);
                mIntent.putExtra("nsen", nsen);
                startActivityForResult(mIntent, 1);
            	break;
            }
            case R.id.mnsel: 
            {
                Intent mIntent=new Intent(this, SensorPref.class);
                mIntent.putExtra("snames", sName);
                mIntent.putExtra("nsen", nsen);
                mIntent.putExtra("srate", rate);
                mIntent.putExtra("act_sen", act_sen);
                startActivityForResult(mIntent, 2);
                break;
            }
        }
	        
	    return true;
	    }
 
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {		//Read the new preferences
        	set_prefs(act_sen, rate);
        }
	 }
	 
	 
	 private void set_prefs(int[] acsen, int srate) {
		 SharedPreferences prefs = getSharedPreferences(getPackageName()+"_preferences", MODE_PRIVATE);
		 
		 //Get the rate
		 srate=Integer.parseInt(prefs.getString("rate_preference", "-1"));
		 
		 if (srate==-1)	{	//No preference is found use deafault values and create pref file
			 srate=SensorManager.SENSOR_DELAY_UI;
			 
			 //By default set acc, gyro, temperature and compass as active sensors if they exist
	        for (int i=0;i<nsen;i++) {
	        	int typ=sList.get(i).getType();
	        	switch (typ) {
	        	case Sensor.TYPE_ACCELEROMETER:
	        		act_sen[i]=1;
	        		break;
	        	case Sensor.TYPE_GYROSCOPE:
	        		act_sen[i]=1;
	        		break;
	        	case Sensor.TYPE_MAGNETIC_FIELD:
	        		act_sen[i]=1;
	        		break;
	        	case Sensor.TYPE_TEMPERATURE:
	        		act_sen[i]=1;
	        		break;
	        	}
	        }
	       
	        //Write these default values to the preference file
	        SharedPreferences.Editor prefsEditor = prefs.edit();
	        prefsEditor.putString("rate_preference", Integer.toString(srate));
	        
	        boolean val;
	        for (int i=0;i<nsen;i++) {
	        	val= (act_sen[i]!=0);
	        	prefsEditor.putBoolean(sName[i], val);
	        }
	        prefsEditor.commit();  
		 }
		 else {
			 //Read directly from pref file
			//active sensors
        	for (int i=0;i<nsen;i++) {
        		boolean bb=prefs.getBoolean(sName[i], false);
        		if (bb)
        			acsen[i]=1;
        		else
        			acsen[i]=0;
        	}
		 }
			 
     	
		 //Print the new values to the debug screen
		 debugview("Rate :" + Integer.toString(srate));
		 for (int i=0;i<nsen;i++) {
			 if (acsen[i]==1) {
				 debugview(Integer.toString(i+1)+ ":" + sName[i]);
			 }
		 }
			 
		 debugview("Active Sensors :"); 
	 }
	 
	 
	 
	 private void register_sensors(int[] acsen, int srate) {
		 //unregister all sensors (if any)
		 sMan.unregisterListener(this);
		 
		 //register the sensors
		 for (int i=0;i<nsen;i++) {
			 if (act_sen[i]==1) {
				 sMan.registerListener(this, sList.get(i), srate);
			 }
		 }
	 }
}