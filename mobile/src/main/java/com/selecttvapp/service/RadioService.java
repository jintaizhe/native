//package com.selecttvapp.service;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.URL;
//import java.net.URLConnection;
//import java.util.LinkedList;
//import java.util.List;
//
//import android.app.Notification;
//import android.app.PendingIntent;
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.content.res.Resources;
//import android.graphics.PixelFormat;
//import android.graphics.Point;
//import android.media.AudioManager;
//import android.media.MediaPlayer;
//import android.media.MediaPlayer.OnErrorListener;
//import android.media.MediaPlayer.OnPreparedListener;
//import android.os.AsyncTask;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.IBinder;
//import android.support.v4.app.NotificationCompat;
//import android.telephony.PhoneStateListener;
//import android.telephony.TelephonyManager;
//import android.util.DisplayMetrics;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.selecttvapp.R;
//
//import com.selecttvapp.ui.activities.MainActivity;
//import com.selecttvapp.ui.views.DynamicImageView;
//
//import org.xmlpull.v1.XmlPullParser;
//import org.xmlpull.v1.XmlPullParserException;
//import org.xmlpull.v1.XmlPullParserFactory;
//
//
//public class RadioService extends Service implements OnPreparedListener,
//		OnErrorListener {
//
//	WindowManager windowManager;
//	View mainView;
//	RelativeLayout linearTopParent;
//	DynamicImageView imageThumgnail;
//	TextView txtChannelName;
//	ImageView imageClose;
//	Button btnPlayStop;
//	int adViewHeight = 0;
//	boolean bAdd = false;
//	private Point szWindow = new Point();
//
//	MediaPlayer player;
//	String url, chnlName, image;
//	//int image;
//	Bundle bundle;
//	PhoneStateListener psl;
//	TelephonyManager tManager;
//
//	Intent intent;
//
//
//	public static final String ACTION_RESUME = "com.softtechbangladesh.banglaradio.service.action.resume";
//	public static final String ACTION_PAUSE = "com.softtechbangladesh.banglaradio.service.action.pause";
//	public static final String ACTION_SETUP_AND_PLAY = "com.softtechbangladesh.banglaradio.service.action.setupandplay";
//
//	public static final String RECIEVER_ACTION_PLAYING = "com.softtechbangladesh.banglaradio.playing";
//	public static final String RECIEVER_ACTION_STOPPED = "com.softtechbangladesh.banglaradio.paused";
//	public static final String RECIEVER_ACTION_PREPARING = "com.softtechbangladesh.banglaradio.preparing";
//	public static final String RECIEVER_ACTION_CLOSE = "com.softtechbangladesh.banglaradio.close";
//	public static final String RECIEVER_ACTION_PREPARE_ERROR = "com.softtechbangladesh.banglaradio.prepareerror";
//
//	@Override
//	public IBinder onBind(Intent intent) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public void onCreate() {
//		// TODO Auto-generated method stub
//		super.onCreate();
//
//		player = new MediaPlayer();
//		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
//		handlePhoneCall();
//		player.setOnPreparedListener(this);
//		player.setOnErrorListener(this);
//	}
//	public static float convertDpToPixel(float dp, Context context){
//		Resources resources = context.getResources();
//		DisplayMetrics metrics = resources.getDisplayMetrics();
//		float px = dp * (metrics.densityDpi / 160f);
//		return px;
//	}
//
//	@Override
//	public int onStartCommand(Intent intent, int flags, int startId) {
//		// TODO Auto-generated method stub
//		bundle = intent.getExtras();
//		//image = bundle.getInt("IMG", android.R.drawable.presence_audio_busy);
//		url = bundle.getString("url");//"http://streema.com/radios/play/1980";//bundle.getString("url");
//		chnlName = bundle.getString("name");
//		image = bundle.getString("image");
//		adViewHeight = bundle.getInt("height");
//		this.intent = intent;
//
//		if( url.endsWith(".asx") ){
//			new ASXParser(url).execute();
//		}else if( url.endsWith(".pls")) {
//			List<String> urls = new PlsParser(url).getUrls();
//			if( urls != null && urls.size() > 0 )
//				url = urls.get(0);
//			initService();
//		}else{
//				initService();
//		}
//		return START_NOT_STICKY;
//	}
//	private void initService(){
//		if (intent.getAction() == ACTION_SETUP_AND_PLAY) {
//
//			windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
//			LayoutInflater inflater = (LayoutInflater)MainActivity.m_gContext.getSystemService(MainActivity.m_gContext.LAYOUT_INFLATER_SERVICE);
//			mainView = (View)inflater.inflate(R.layout.radio_thumbview, null);
//			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//				windowManager.getDefaultDisplay().getSize(szWindow);
//			} else {
//				int w = windowManager.getDefaultDisplay().getWidth();
//				int h = windowManager.getDefaultDisplay().getHeight();
//				szWindow.set(w, h);
//			}
//
//			linearTopParent = (RelativeLayout)mainView.findViewById(R.id.linearTopParent);
//			btnPlayStop = (Button)mainView.findViewById(R.id.btnRadioPlayStop);
//			txtChannelName = (TextView)mainView.findViewById(R.id.txtChannelName);
//			imageClose = (ImageView)mainView.findViewById(R.id.imageClose);
//			imageThumgnail = (DynamicImageView)mainView.findViewById(R.id.imageThumbnail);
//
//			imageThumgnail.loadImage(image);
//			txtChannelName.setText(chnlName);
//
//			LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) linearTopParent.getLayoutParams();
//			param.width = szWindow.x;
//			param.height = (int) convertDpToPixel(70, MainActivity.m_gContext);
//
//			btnPlayStop.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					if( btnPlayStop.getText().toString().equals("Play") ){
//						playerResume();
//					}else{
//						playerPause();
//					}
//				}
//			});
//
//			imageClose.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					RadioService.this.stopSelf();
//				}
//			});
//
//			playerSetUpAndPlay();
//
//		} else if (intent.getAction() == ACTION_RESUME) {
//
//			playerResume();
//
//		} else if (intent.getAction() == ACTION_PAUSE) {
//
//			playerPause();
//
//		}
//	}
//
//	private void handlePhoneCall() {
//		psl = new PhoneStateListener() {
//
//			@Override
//			public void onCallStateChanged(int state, String incomingNumber) {
//
//				Log.e("PhonState", "Changed");
//				if (state == TelephonyManager.CALL_STATE_RINGING) {
//					// Incoming call: Pause music
//					Log.e("PhonState", "Ringing");
//					if (player != null) {
//						if (player.isPlaying()) {
//
//							player.pause();
//
//						}
//					}
//				} else if (state == TelephonyManager.CALL_STATE_IDLE) {
//					// Not in call: Play music
//					Log.e("PhonState", "Idle");
//					if (player != null) {
//						if (!player.isPlaying()) {
//							player.start();
//
//						}
//					}
//
//				} else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
//					// A call is dialing, active or on hold
//					Log.e("PhonState", "Dialing");
//					if (player != null) {
//						if (player.isPlaying()) {
//
//							player.pause();
//
//						}
//					}
//				}
//
//				// TODO Auto-generated method stub
//				super.onCallStateChanged(state, incomingNumber);
//			}
//
//		};
//		tManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//		if (tManager != null) {
//			tManager.listen(psl, PhoneStateListener.LISTEN_CALL_STATE);
//		}
//
//	}
//
//	@Override
//	public void onDestroy() {
//		// TODO Auto-generated method stub
//		super.onDestroy();
//
//		playerStopANdRealease();
//
//	}
//
//	@Override
//	public void onPrepared(MediaPlayer mp) {
//		player.start();
//		if (player.isPlaying()) {
//
//			pushServicetoForeground();
//
//
//			//if( adViewHeight == 0 )
//				adViewHeight = (int) convertDpToPixel(150, MainActivity.m_gContext);
//		//	else
//		//		adViewHeight = (int) convertDpToPixel(adViewHeight, MainActivity.m_gContext);
//			addWindow(mainView, 0, szWindow.y - adViewHeight - (int) convertDpToPixel(70, MainActivity.m_gContext));
//			btnPlayStop.setText("Stop");
//			broadCast(RECIEVER_ACTION_PLAYING);
//
//		}
//
//	}
//	public void addWindow(View view, int x, int y){
//		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//				WindowManager.LayoutParams.WRAP_CONTENT,
//				WindowManager.LayoutParams.WRAP_CONTENT,
//				WindowManager.LayoutParams.TYPE_PHONE,
//				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//				PixelFormat.TRANSLUCENT);
//		params.gravity = Gravity.TOP | Gravity.LEFT;
//		params.x = x;
//		params.y = y;
//
//		windowManager.addView(view, params);
//		bAdd = true;
//	}
//	private void pushServicetoForeground() {
//		final int notiId = 1234;
//		Notification notice;
//		NotificationCompat.Builder builder = new NotificationCompat.Builder(
//				this).setSmallIcon(R.drawable.ic_launcher)
//				.setContentTitle(getResources().getString(R.string.app_name))
//				.setContentText("Playing: " + chnlName).setAutoCancel(true);
//
//		Intent notificationIntent = new Intent(this, MainActivity.class);
//
//		notificationIntent.putExtra("BUNDLE", bundle);
//		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
//				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//		builder.setContentIntent(contentIntent);
//		notice = builder.build();
//		startForeground(notiId, notice);
//
//	}
//
//	@Override
//	public boolean onError(MediaPlayer mp, int what, int extra) {
//
//		broadCast(RECIEVER_ACTION_PREPARE_ERROR);
//		return true;
//	}
//
//	private void playerPause() {
//		if (player != null) {
//
//			if (player.isPlaying()) {
//
//				player.pause();
//
//				broadCast(RECIEVER_ACTION_STOPPED);
//				btnPlayStop.setText("Play");
//
//			}
//		}
//
//	}
//
//	private void playerResume() {
//		if (player != null) {
//
//			if (!player.isPlaying()) {
//
//				player.start();
//				broadCast(RECIEVER_ACTION_PLAYING);
//				btnPlayStop.setText("Stop");
//
//			}
//
//		}
//
//	}
//
//	private void broadCast(String recieverAction) {
//		Intent broadcastIntent = new Intent();
//		broadcastIntent.setAction(recieverAction);
//		sendBroadcast(broadcastIntent);
//
//	}
//
//	private void playerSetUpAndPlay() {
//		try {
//			if (player != null) {
//				if (!player.isPlaying()) {
//					player.setDataSource(url);
//					player.prepareAsync();
//
//					broadCast(RECIEVER_ACTION_PREPARING);
//
//				}
//
//			}
//		} catch (IllegalArgumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (SecurityException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalStateException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
//
//	private void playerStopANdRealease() {
//		if (player != null) {
//
//			if (player.isPlaying()) {
//
//				player.stop();
//
//			}
//			player.release();
//			player = null;
//
//			broadCast(RECIEVER_ACTION_CLOSE);
//
//			if( bAdd )
//				windowManager.removeView(mainView);
//			bAdd = false;
//
//		}
//
//	}
//
//
//	public class ASXParser extends AsyncTask {
//
//		URL asxUrl;
//		String pathUrl;
//		String streamUrl;
//		XmlPullParser xmlpullparser;
//		ASXParser(String pathUrl){
//			this.pathUrl = pathUrl;
//			this.streamUrl = pathUrl;
//		}
//		void parseTag(int event){
//
//			switch (event) {
//
//				case XmlPullParser.START_DOCUMENT:
//					Log.i("","START_DOCUMENT");
//					break;
//
//				case XmlPullParser.END_DOCUMENT:
//					Log.i("","END_DOCUMENT");
//					break;
//				case XmlPullParser.START_TAG:
//					if( xmlpullparser.getName().toLowerCase().equals("ref") ){
//						String temp = xmlpullparser.getAttributeValue(null, "href");
//						if( temp != null )
//							streamUrl = temp;
//					}
//					break;
//
//				case XmlPullParser.END_TAG:
//					Log.i("","END_TAG : "+xmlpullparser.getName());
//					break;
//
//				case XmlPullParser.TEXT:
//					Log.i("","TEXT");
//					String output = xmlpullparser.getText();
//					Log.i("valuee : ",""+output);
//					break;
//			}
//
//		}
//		public InputStream getInputStream(URL url) {
//			try {
//				return url.openConnection().getInputStream();
//			} catch (IOException e) {
//				return null;
//			}
//		}
//
//		@Override
//		protected Object doInBackground(Object[] objects) {
//			// Initializing instance variables
//			try {
//
//				asxUrl = new URL(pathUrl);
//				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
//				factory.setNamespaceAware(true);
//				xmlpullparser = factory.newPullParser();
//				xmlpullparser.setInput(new InputStreamReader(
//						getInputStream(asxUrl)));
//
//				int eventType = 0;
//				try {
//					eventType = xmlpullparser.getEventType();
//				} catch (XmlPullParserException e) {
//					e.printStackTrace();
//				}
//				while (eventType != XmlPullParser.END_DOCUMENT) {
//
//					parseTag(eventType);
//					try {
//						eventType = xmlpullparser.next();
//					} catch (XmlPullParserException e) {
//						e.printStackTrace();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//			} catch (XmlPullParserException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Object params) {
//			url = this.streamUrl;
//			initService();
//		}
//	}
//	public class PlsParser {
//		private BufferedReader reader = null;
//
//		public PlsParser(String url) {
//
//			try {
//				URLConnection urlConnection = new URL(url).openConnection();
//				this.reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//
//		public List<String> getUrls() {
//			LinkedList<String> urls = new LinkedList<String>();
//			while (true) {
//				try {
//					String line = reader.readLine();
//					if (line == null) {
//						break;
//					}
//					String url = parseLine(line);
//					if (url != null && !url.equals("")) {
//						urls.add(url);
//					}
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//			return urls;
//		}
//
//		private String parseLine(String line) {
//			if (line == null) {
//				return null;
//			}
//			String trimmed = line.trim();
//			if (trimmed.indexOf("http") >= 0) {
//				return trimmed.substring(trimmed.indexOf("http"));
//			}
//			return "";
//		}
//	}
//}
