package com.example.barcodescanning;

import android.support.v7.app.ActionBarActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener
{

	private Button scanBtn,btnAdd,btnConfirm;
	private Button btnSetup;
	private TextView formatTxt, contentTxt;
	String Content;
	String ID;
	private TextView txtPrice,txtSum;
	private EditText edtip,edtid,edtport,edtCount;
	static String Message	="";
	String buyed="";
	String scanContent="";
	int sum=0;
	Date now;
	
	protected void onCreate(final Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    if(read()==null)
		{
	     setContentView(R.layout.setup); 
	     btnSetup = (Button)findViewById(R.id.btnsetup);
	     edtip=(EditText)findViewById(R.id.edtip);
	     edtid=(EditText)findViewById(R.id.edtid);
	     edtport=(EditText)findViewById(R.id.edtport);
		 btnSetup.setOnClickListener(new OnClickListener(){
		    	

			@Override
			public void onClick(View v) {
					write(edtport.getText().toString(),edtip.getText().toString(),edtid.getText().toString());
					
				create(savedInstanceState);
				
			}
				
			});
		}
	    else
	    {
	    	create(savedInstanceState);
	    }
	   
	}
	protected void create(Bundle savedInstanceState)
	{
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.activity_main);
		    scanBtn = (Button)findViewById(R.id.scan_button);
		    formatTxt = (TextView)findViewById(R.id.scan_format);
		    contentTxt = (TextView)findViewById(R.id.scan_content);
		    txtPrice=(TextView)findViewById(R.id.txtPrice);
		    btnAdd = (Button)findViewById(R.id.btnAdd);
		    txtSum=(TextView)findViewById(R.id.txtSum);
		    btnConfirm=(Button)findViewById(R.id.btnConfirm);
		    edtCount=(EditText)findViewById(R.id.edtCount);
			scanBtn.setOnClickListener(this);
			 String []_read=new String[3];
		      _read=read();
		      ID=_read[2];
			/*txtPrice.setText(Message);
			 AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);

	          dlgAlert.setMessage(Message+"...");
	          dlgAlert.setTitle("Error Message...");
	          dlgAlert.setPositiveButton("OK", null);
	          dlgAlert.setCancelable(true);
	          dlgAlert.create().show();*/

			btnAdd.setOnClickListener(new OnClickListener()
			{
			@Override
			public void onClick(View v)
			{
				if(scanContent!="")
				{
					now=new Date();
				SimpleDateFormat ft = new SimpleDateFormat ("E'*'yyyy.MM.dd'at'hh:mm:ss");
				String dt=ft.format(now);
				buyed+=ID+" "+scanContent+" "+dt+" "+edtCount.getText().toString()+"\n";
				}
				int current=0;
				if(txtPrice.getText().toString()!="")
				{
					current=Integer.parseInt(txtPrice.getText().toString())*Integer.parseInt(edtCount.getText().toString());
				}
			   if(txtSum.getText().toString()!="")
				{
					sum=Integer.parseInt(txtSum.getText().toString());
				}
				sum=sum+current;
				formatTxt.setText("");
				contentTxt.setText("");
				txtPrice.setText("");
				scanContent="";
				txtSum.setText(String.valueOf(sum));
				Message="";
				
			}
			
				
			});
			btnConfirm.setOnClickListener(new OnClickListener()
					{
				     public void onClick(View v)
				     {
				    	 Thread t1=new Thread(new Runnable() 
			             {

			                 @Override
			                 public void run()
			                 {
			                 	 Client(buyed); 
			                 }	             
			             });
						 t1.start();
						 txtPrice.setText("");
						 txtSum.setText("");
						 edtCount.setText("");
						 sum=0;
						 buyed="";
					
				     }
				
					});
	}
	
	
	

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.scan_button)
		{
			IntentIntegrator scanIntegrator = new IntentIntegrator(this);
			scanIntegrator.initiateScan();
		}		
	}
	public void onActivityResult(int requestCode, int resultCode, Intent intent) 
	{
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanningResult != null) {
			scanContent = scanningResult.getContents();
			String scanFormat = scanningResult.getFormatName();
			Content=scanContent;
			formatTxt.setText(scanFormat);
			contentTxt.setText(scanContent);
			 Thread t1=new Thread(new Runnable() 
             {

                 @Override
                 public void run()
                 {
                 	 Client(scanContent); 
                 }	             
             });
			 t1.start();
			 try 
			 {
				t1.join();
			} catch (InterruptedException e)
			 {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 txtPrice.setText(Message);
			 edtCount.setText("1");
			}
		else{
		    Toast toast = Toast.makeText(getApplicationContext(), 
		        "No scan data received!", Toast.LENGTH_SHORT);
		    toast.show();
		}
	}
	public String GetInfo()
	{
		String fileName = "/sdcard/temp.txt";
		String line=null;
	    String info = null;
	    String error=null;
	    try 
	     {
	         // FileReader reads text files in the default encoding.
	         FileReader fileReader = new FileReader(fileName);

	         // Always wrap FileReader in BufferedReader.
	         BufferedReader bufferedReader = new BufferedReader(fileReader);

	         while((line = bufferedReader.readLine()) != null){
	        	 info+=line+"\n";
	         }   

	         // Always close files.
	         bufferedReader.close();         
	     }
	     catch(FileNotFoundException ex1){
	    	// toast = Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
	           error=  "Unable to open file   "+fileName;
	           info=null;
	          //   fileName + "'");                
	     }
	     catch(IOException ex)
	     {
	    	 Toast toast = Toast.makeText(getApplicationContext(), 
	 		        "Error reading file"+fileName, Toast.LENGTH_SHORT);
	         error=  "Error reading file "+fileName;
	         info=null;
	         // Or we could just do this: 
	         // ex.printStackTrace();
	         }
	     Message=info;
	     return info;
	}
	public void SetInfo(String port,String IP,String ID) throws FileNotFoundException, UnsupportedEncodingException
	{
		String fileName = "/sdcard/temp.txt";
		PrintWriter writer = new PrintWriter(fileName, "UTF-8");

        try {
            // Assume default encoding.
            FileWriter fileWriter =
                new FileWriter(fileName);

            // Always wrap FileWriter in BufferedWriter.
            BufferedWriter bufferedWriter =
                new BufferedWriter(fileWriter);

            // Note that write() does not automatically
            // append a newline character.
            bufferedWriter.write(port);
            bufferedWriter.write("\n"+IP);
            bufferedWriter.newLine();
            bufferedWriter.write("\n"+ID);

            // Always close files.
            bufferedWriter.close();
        }
        catch(IOException ex) {
        	 Toast toast = Toast.makeText(getApplicationContext(), 
 	 		        "Error writing to  file"+fileName, Toast.LENGTH_SHORT);
            // Or we could just do this:
            // ex.printStackTrace();
        }
	    }
	
	public String[] read(){
		 
        BufferedReader br = null;
        String response = null;
        String[]_return=new String[3];

           try {

               StringBuffer output = new StringBuffer();
               String fpath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()+ "/%Scanning%/info.txt";

               br = new BufferedReader(new FileReader(fpath));
               String line = "";
               int k=0;
               while ((line = br.readLine()) != null) {
            	   _return[k]=line;
            	   k++;
                   output.append(line +"\n");
               }
               response = output.toString();

           } catch (IOException e) {
               e.printStackTrace();
               return null;

           }
           return _return;

    }
	 public Boolean write(String port, String IP,String ID){
         try {
        	 File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()+ "/%Scanning%/");
        	 if (!(dir.exists() && dir.isDirectory())) {
        	     dir.mkdirs();
        	 }


        	 File file = new File(dir, "info"+".txt");
             // If file does not exists, then create it
             if (!file.exists()) {
                 file.createNewFile();
             }

             FileWriter fw = new FileWriter(file.getAbsoluteFile());
             BufferedWriter bw = new BufferedWriter(fw);
             bw.write(port);
             bw.write("\n"+IP);
             bw.write("\n"+ID);
             bw.close();

           //  Log.d("Suceess","Sucess");
             return true;

         } catch (IOException e) {
             e.printStackTrace();
             return false;
         }

  }
	 public String  testClient()
	 {
	       String finish = null;
	      String serverip ;
	      String []_read=new String[3];
	      _read=read();
	      serverip=_read[1];
	      int port = Integer.parseInt(_read[0]);
	      String msg = null;
	    
	      try
	      {
	         
	       /*  System.out.println("Connecting to " + serverip +
			 " on port " + port);*/
	         Socket client = new Socket(serverip, port);
	         OutputStream outToServer = client.getOutputStream();
	         DataOutputStream out = new DataOutputStream(outToServer);
	         finish="....";
	         out.writeUTF(finish
	                      + client.getLocalSocketAddress());
	         InputStream inFromServer = client.getInputStream();
	         DataInputStream in =
	                        new DataInputStream(inFromServer);
	         msg+="Server says " + in.readUTF().toString();
	         client.close();
	      }catch(IOException e)
	      {
	         e.printStackTrace();
	      }
	      
	    /*  AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);

          dlgAlert.setMessage(msg+"...");
          dlgAlert.setTitle("Error Message...");
          dlgAlert.setPositiveButton("OK", null);
          dlgAlert.setCancelable(true);
          dlgAlert.create().show();

         /* dlgAlert.setPositiveButton("Ok",
              new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {

              }
          });*/
	      Message=msg;
	      return msg;
	 }
	 public String  Client(String input)
	 {
	      String serverip ;
	      String []_read=new String[3];
	      _read=read();
	      serverip=_read[1];
	      int port = Integer.parseInt(_read[0]);
	      String msg ="";
	    
	      try
	      {
	         
	       /*  System.out.println("Connecting to " + serverip +
			 " on port " + port);*/
	         Socket client = new Socket(serverip, port);
	         OutputStream outToServer = client.getOutputStream();
	         DataOutputStream out = new DataOutputStream(outToServer);
	        
	         out.writeUTF(input);
	         InputStream inFromServer = client.getInputStream();
	         DataInputStream in =
	                        new DataInputStream(inFromServer);
	         msg+=in.readUTF().toString();
	         client.close();
	      }catch(IOException e)
	      {
	         e.printStackTrace();
	      }
	      
	    /*  AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);

          dlgAlert.setMessage(msg+"...");
          dlgAlert.setTitle("Error Message...");
          dlgAlert.setPositiveButton("OK", null);
          dlgAlert.setCancelable(true);
          dlgAlert.create().show();

         /* dlgAlert.setPositiveButton("Ok",
              new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {

              }
          });*/
	      Message=msg;
	      return msg;
	 }
}


	
