package com.example.ukeeper;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class SendMailActivity extends ActionBarActivity {
	private final static String CLEAN_PAGE_EMAIL = "drops@ukeeper.com";
	private final static String FULL_PAGE_EMAIL = "drops.full@ukeeper.com";
	private final static String SYSTEM_MODE = "system";
	private final static String BUILTIN_MODE = "builtin";
	private String email = CLEAN_PAGE_EMAIL;
	private EditText urlEdit;
	private String mode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_mail);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}

	}

	@Override
	protected void onStart() {
		super.onStart();
		Intent intent = getIntent();
		String action = intent.getAction();
		String type = intent.getType();

		if (Intent.ACTION_SEND.equals(action) && type != null) {
			if ("text/plain".equals(type)) {
				String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
				if (sharedText != null) {
					// Create the text view
					urlEdit = (EditText) findViewById(R.id.url);
					urlEdit.setText(sharedText);
				}
			}
		}
		
		Log.e("Ыть", getSharedPreferences("My",Context.MODE_PRIVATE).getString(getString(R.string.key_username),new String("бля")));

		mode = getSharedPreferences("My",Context.MODE_PRIVATE).getString(
				getString(R.string.key_mode), SYSTEM_MODE);
	}

	// change page type (clean or full)
	public void onPageTypeClicked(View view) {
		// Is the button now checked?
		boolean checked = ((RadioButton) view).isChecked();

		// Check which radio button was clicked
		switch (view.getId()) {
		case R.id.radio_clean_page:
			if (checked)
				email = CLEAN_PAGE_EMAIL;
			break;
		case R.id.radio_full_page:
			if (checked)
				email = FULL_PAGE_EMAIL;
			break;
		}
	}

	// send button clicked
	public void onSendClicked(View view) {

		if (mode.equals(SYSTEM_MODE)) {
			Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("message/rfc822");
			i.putExtra(Intent.EXTRA_EMAIL, new String[] { email });
			i.putExtra(Intent.EXTRA_SUBJECT, "page");
			i.putExtra(Intent.EXTRA_TEXT, urlEdit.getText());
			try {
				startActivity(Intent.createChooser(i, "Send mail..."));
			} catch (android.content.ActivityNotFoundException ex) {
				Toast.makeText(SendMailActivity.this,
						"There are no email clients installed.",
						Toast.LENGTH_SHORT).show();
			}
		} else {
			sendMail(email,"page",urlEdit.getText().toString());
		}
		//finish();
	}

	// create new session for mail sending
	private Session createSessionObject() {
		Properties properties = new Properties();
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.port", "587");

		return Session.getInstance(properties, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(
						SendMailActivity.this.getSharedPreferences("My",
								Context.MODE_PRIVATE).getString(
								getString(R.string.key_username), new String()),
						SendMailActivity.this.getSharedPreferences("My",
								Context.MODE_PRIVATE).getString(
								getString(R.string.key_password), new String()));
			}
		});
	}

	// create mail message
	private Message createMessage(String email, String subject,
			String messageBody, Session session) throws MessagingException,
			UnsupportedEncodingException {
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(SendMailActivity.this
				.getSharedPreferences("My",Context.MODE_PRIVATE).getString(
						getString(R.string.key_username), new String()),
				SendMailActivity.this.getSharedPreferences("My",Context.MODE_PRIVATE)
						.getString(getString(R.string.key_username),
								new String())));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(
				email, email));
		message.setSubject(subject);
		message.setText(messageBody);
		return message;
	}

	// send mail async
	private class SendMailTask extends AsyncTask<Message, Void, Void> {
		private ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(SendMailActivity.this,
					"Please wait", "Sending mail", true, false);
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			progressDialog.dismiss();
			SendMailActivity.this.finish();
		}

		@Override
		protected Void doInBackground(Message... messages) {
			try {
				Transport.send(messages[0]);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	private void sendMail(String email, String subject, String messageBody) {
		Session session = createSessionObject();

		try {
			Message message = createMessage(email, subject, messageBody,
					session);
			new SendMailTask().execute(message);
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.send_mail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_send_mail,
					container, false);
			return rootView;
		}
	}

}
