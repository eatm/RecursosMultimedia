package com.example.resursosmultimedia;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.Switch;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	public static class PlaceholderFragment extends Fragment implements
			SurfaceHolder.Callback, OnPreparedListener {
		private MediaRecorder mediaRecorder = null;
		private MediaRecorder mediaRecorderAudio = null;
		private MediaPlayer mediaPlayer = null;
		private MediaPlayer mediaPlayerAudio = null;
		private String fileNameVideo = null;
		private String fileNameAudio = null;
		private boolean recording = false;
		private boolean toRecord = true;
		private View rootView;
		private ImageButton btnRec;
		private ImageButton btnStop;
		private ImageButton btnPlay;
		private ImageButton btnPlayStreaming;
		private Switch recordType;
		private SurfaceView surface;
		private SurfaceHolder holder;
		private String videoUrl = "https://archive.org/download/TutoriavjvjjvewatToStudyAfterCompletingBasicJava/Lect%205%20-a-hello-world-program.mp4";
		
		public PlaceholderFragment() {
		}

		@SuppressWarnings("deprecation")
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			fileNameVideo = Environment.getExternalStorageDirectory()
					+ "/video.mp4";
			fileNameAudio = Environment.getExternalStorageDirectory()
					+ "/audio.3gp";

			recordType = (Switch) rootView.findViewById(R.id.type_record);
			btnRec = (ImageButton) rootView.findViewById(R.id.btRec);
			btnStop = (ImageButton) rootView.findViewById(R.id.btStop);
			btnStop.setEnabled(false);
			btnPlay = (ImageButton) rootView.findViewById(R.id.btPlay);
			btnPlay.setEnabled(false);
			btnPlayStreaming = (ImageButton) rootView
					.findViewById(R.id.btPlayStream);

			surface = (SurfaceView) rootView.findViewById(R.id.surface);
			holder = surface.getHolder();
			holder.addCallback(this);
			holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

			recordType.setChecked(true);
			recordType
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							if (isChecked) {
								btnRec.setImageResource(android.R.drawable.ic_menu_camera);
								toRecord = true;
							} else {
								btnRec.setImageResource(android.R.drawable.ic_btn_speak_now);
								toRecord = false;
							}
						}
					});

			btnRec.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					btnRec.setEnabled(false);
					btnStop.setEnabled(true);
					btnPlay.setEnabled(false);
					recordType.setEnabled(false);
					btnPlayStreaming.setEnabled(false);

					if (toRecord == true) {
						recordVideo();
					} else {
						recordAudio();
					}
				}
			});

			btnStop.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					btnRec.setEnabled(true);
					btnStop.setEnabled(false);
					btnPlay.setEnabled(true);
					recordType.setEnabled(false);

					if (toRecord == true) {
						stopVideo();
					} else {
						stopAudio();
					}
				}
			});

			btnPlay.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					btnRec.setEnabled(false);
					btnStop.setEnabled(true);
					btnPlay.setEnabled(false);
					recordType.setEnabled(false);
					btnPlayStreaming.setEnabled(false);

					if (toRecord == true) {
						playVideo();
					} else {
						playAudio();
					}
				}
			});

			btnPlayStreaming.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					btnRec.setEnabled(false);
					btnStop.setEnabled(false);
					btnPlay.setEnabled(false);
					recordType.setEnabled(false);

					playStreaming();
				}
			});

			return rootView;
		}

		public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2,
				int arg3) {
		}

		public void surfaceCreated(SurfaceHolder holder) {
			if (mediaRecorder == null) {
				mediaRecorder = new MediaRecorder();
				mediaRecorder.setPreviewDisplay(holder.getSurface());
			}

			if (mediaPlayer == null) {
				mediaPlayer = new MediaPlayer();
				mediaPlayer.setDisplay(holder);
			}
		}

		public void surfaceDestroyed(SurfaceHolder arg0) {
			mediaRecorder.release();
			mediaPlayer.release();
		}

		public void recordVideo() {
			prepareVideoRecorder();
			try {
				mediaRecorder.prepare();
				mediaRecorder.start();
				recording = true;
			} catch (IllegalStateException e) {
			} catch (IOException e) {
			}
		}

		public void stopVideo() {
			if (recording) {
				recording = false;
				mediaRecorder.stop();
				mediaRecorder.reset();

			} else if (mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
				mediaPlayer.reset();
				// Finalmente se borra el archivo almacenado
				File file = new File(Environment.getExternalStorageDirectory(),
						"video.mp4");
				@SuppressWarnings("unused")
				boolean deleted = file.delete();
				btnRec.setEnabled(true);
				btnPlay.setEnabled(false);
				recordType.setEnabled(true);
				btnPlayStreaming.setEnabled(true);
			}
		}

		public void playVideo() {
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					btnRec.setEnabled(true);
					btnStop.setEnabled(false);
					btnPlay.setEnabled(true);
					mediaPlayer.reset();
				}
			});
			try {
				mediaPlayer.setDataSource(fileNameVideo);
				mediaPlayer.prepare();
			} catch (IllegalStateException e) {
			} catch (IOException e) {
			}
			mediaPlayer.start();
		}

		public void recordAudio() {
			prepareAudioRecorder();
			try {
				mediaRecorderAudio.prepare();
				mediaRecorderAudio.start();
				recording = true;
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		public void stopAudio() {
			if (recording) {
				mediaRecorderAudio.stop();
				mediaRecorderAudio.reset();
				recording = false;
			} else if (mediaPlayerAudio != null) {
				mediaPlayerAudio.stop();
				mediaPlayerAudio.reset();

				// Finalmente se borra el archivo almacenado
				File file = new File(Environment.getExternalStorageDirectory(),
						"audio.3gp");
				@SuppressWarnings("unused")
				boolean deleted = file.delete();				
				btnRec.setEnabled(true);
				btnPlay.setEnabled(false);
				recordType.setEnabled(true);
				btnPlayStreaming.setEnabled(true);
			}
		}

		public void playAudio() {
			mediaPlayerAudio = new MediaPlayer();
			mediaPlayerAudio
					.setOnCompletionListener(new OnCompletionListener() {
						@Override
						public void onCompletion(MediaPlayer mp) {
							btnRec.setEnabled(true);
							btnStop.setEnabled(false);
							btnPlay.setEnabled(true);
							mediaPlayerAudio.reset();
						}
					});

			try {
				mediaPlayerAudio.setDataSource(fileNameAudio);
			} catch (IllegalArgumentException e1) {
				e1.printStackTrace();
			} catch (SecurityException e1) {
				e1.printStackTrace();
			} catch (IllegalStateException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				mediaPlayerAudio.prepare();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			mediaPlayerAudio.start();
		}

		public void playStreaming() {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
				mediaPlayer.reset();
				btnRec.setEnabled(true);
				recordType.setEnabled(true);
			} else {
				mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						btnRec.setEnabled(true);
						recordType.setEnabled(true);
						mediaPlayer.reset();
					}
				});
				try {
					mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
					mediaPlayer.setDataSource(videoUrl);
					mediaPlayer.prepare();
					mediaPlayer.setOnPreparedListener(this);
				} catch (IllegalStateException e) {
				} catch (IOException e) {
				}
			}
		}

		public void prepareVideoRecorder() {
			mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
			mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
			mediaRecorder.setOutputFile(fileNameVideo);
		}

		public void prepareAudioRecorder() {
			mediaRecorderAudio = new MediaRecorder();
			mediaRecorderAudio.setAudioSource(MediaRecorder.AudioSource.MIC);
			mediaRecorderAudio
					.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mediaRecorderAudio
					.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mediaRecorderAudio.setOutputFile(fileNameAudio);
		}

		@Override
		public void onPrepared(MediaPlayer mp) {
			mediaPlayer.start();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
