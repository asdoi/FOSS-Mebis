/*
    This file is part of the HHS Moodle WebApp.

    HHS Moodle WebApp is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    HHS Moodle WebApp is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with the Diaspora Native WebApp.

    If not, see <http://www.gnu.org/licenses/>.
 */

package de.baumann.hhsmoodle.popup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import de.baumann.hhsmoodle.R;
import de.baumann.hhsmoodle.helper.Activity_password;
import de.baumann.hhsmoodle.helper.Database_Notes;
import de.baumann.hhsmoodle.helper.class_SecurePreferences;
import de.baumann.hhsmoodle.helper.helper_main;
import de.baumann.hhsmoodle.helper.helper_notes;

public class Popup_notes extends Activity {

    private ListView listView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(Popup_notes.this, R.xml.user_settings, false);
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Popup_notes.this);

        class_SecurePreferences sharedPrefSec = new class_SecurePreferences(Popup_notes.this, "sharedPrefSec", "Ywn-YM.XK$b:/:&CsL8;=L,y4", true);
        String pw = sharedPrefSec.getString("protect_PW");

        if (pw != null  && pw.length() > 0) {
            if (sharedPref.getBoolean("isOpened", true)) {
                helper_main.switchToActivity(Popup_notes.this, Activity_password.class, "", false);
            }
        }

        setContentView(R.layout.activity_popup);

        listView = (ListView)findViewById(R.id.dialogList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                @SuppressWarnings("unchecked")
                HashMap<String,String> map = (HashMap<String,String>)listView.getItemAtPosition(position);

                final String title = map.get("title");
                final String cont = map.get("cont");
                final String seqnoStr = map.get("seqno");
                final String icon = map.get("icon");
                final String attachment = map.get("attachment");
                final String create = map.get("createDate");

                final Button attachment2;
                final TextView textInput;

                LayoutInflater inflater = getLayoutInflater();

                final ViewGroup nullParent = null;
                View dialogView = inflater.inflate(R.layout.dialog_note_show, nullParent);

                final String attName = attachment.substring(attachment.lastIndexOf("/")+1);
                final String att = getString(R.string.app_att) + ": " + attName;

                attachment2 = (Button) dialogView.findViewById(R.id.button_att);
                if (attName.equals("")) {
                    attachment2.setVisibility(View.GONE);
                } else {
                    attachment2.setText(att);
                }
                File file2 = new File(attachment);
                if (!file2.exists()) {
                    attachment2.setVisibility(View.GONE);
                }

                textInput = (TextView) dialogView.findViewById(R.id.note_text_input);
                textInput.setText(cont);
                Linkify.addLinks(textInput, Linkify.WEB_URLS);

                attachment2.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        openAtt(attachment);
                    }
                });

                final ImageView be = (ImageView) dialogView.findViewById(R.id.imageButtonPri);
                assert be != null;

                switch (icon) {
                    case "1":
                        be.setImageResource(R.drawable.circle_green);
                        break;
                    case "2":
                        be.setImageResource(R.drawable.circle_yellow);
                        break;
                    case "3":
                        be.setImageResource(R.drawable.circle_red);
                        break;
                }

                android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(Popup_notes.this)
                        .setTitle(title)
                        .setView(dialogView)
                        .setPositiveButton(R.string.toast_yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton(R.string.note_edit, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                sharedPref.edit()
                                        .putString("handleTextTitle", title)
                                        .putString("handleTextText", cont)
                                        .putString("handleTextIcon", icon)
                                        .putString("handleTextSeqno", seqnoStr)
                                        .putString("handleTextAttachment", attachment)
                                        .putString("handleTextCreate", create)
                                        .apply();
                                helper_notes.editNote(Popup_notes.this);
                            }
                        });
                dialog.show();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                @SuppressWarnings("unchecked")
                HashMap<String,String> map = (HashMap<String,String>)listView.getItemAtPosition(position);

                final String seqnoStr = map.get("seqno");
                final String title = map.get("title");
                final String cont = map.get("cont");
                final String icon = map.get("icon");
                final String attachment = map.get("attachment");
                final String create = map.get("createDate");

                final CharSequence[] options = {
                        getString(R.string.note_edit),
                        getString(R.string.note_share),
                        getString(R.string.bookmark_createEvent),
                        getString(R.string.note_remove_note)};
                new AlertDialog.Builder(Popup_notes.this)
                        .setPositiveButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        })
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {

                                if (options[item].equals(getString(R.string.note_edit))) {
                                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Popup_notes.this);
                                    sharedPref.edit()
                                            .putString("handleTextTitle", title)
                                            .putString("handleTextText", cont)
                                            .putString("handleTextIcon", icon)
                                            .putString("handleTextSeqno", seqnoStr)
                                            .putString("handleTextAttachment", attachment)
                                            .putString("handleTextCreate", create)
                                            .putString("fromPopup", "0")
                                            .apply();
                                    helper_notes.editNote(Popup_notes.this);
                                }

                                if (options[item].equals (getString(R.string.note_share))) {
                                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                                    sharingIntent.setType("text/plain");
                                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, title);
                                    sharingIntent.putExtra(Intent.EXTRA_TEXT, cont);
                                    startActivity(Intent.createChooser(sharingIntent, (getString(R.string.note_share_2))));
                                }

                                if (options[item].equals (getString(R.string.bookmark_createEvent))) {
                                    Intent calIntent = new Intent(Intent.ACTION_INSERT);
                                    calIntent.setType("vnd.android.cursor.item/event");
                                    calIntent.putExtra(CalendarContract.Events.TITLE, title);
                                    calIntent.putExtra(CalendarContract.Events.DESCRIPTION, cont);
                                    startActivity(calIntent);
                                }

                                if (options[item].equals(getString(R.string.note_remove_note))) {
                                    try {
                                        Database_Notes db = new Database_Notes(Popup_notes.this);
                                        final int count = db.getRecordCount();
                                        db.close();

                                        if (count == 1) {
                                            Snackbar snackbar = Snackbar
                                                    .make(listView, R.string.note_remove_cannot, Snackbar.LENGTH_LONG);
                                            snackbar.show();

                                        } else {
                                            Snackbar snackbar = Snackbar
                                                    .make(listView, R.string.note_remove_confirmation, Snackbar.LENGTH_LONG)
                                                    .setAction(R.string.toast_yes, new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            try {
                                                                Database_Notes db = new Database_Notes(Popup_notes.this);
                                                                db.deleteNote(Integer.parseInt(seqnoStr));
                                                                db.close();
                                                                setNotesList();
                                                            } catch (PackageManager.NameNotFoundException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });
                                            snackbar.show();
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }).show();
                return true;
            }
        });

        setNotesList();
    }

    private void setNotesList() {

        ArrayList<HashMap<String,String>> mapList = new ArrayList<>();

        try {
            Database_Notes db = new Database_Notes(Popup_notes.this);
            ArrayList<String[]> bookmarkList = new ArrayList<>();
            db.getBookmarks(bookmarkList, Popup_notes.this);
            if (bookmarkList.size() == 0) {
                db.loadInitialData();
                db.getBookmarks(bookmarkList, Popup_notes.this);
            }
            db.close();

            for (String[] strAry : bookmarkList) {
                HashMap<String, String> map = new HashMap<>();
                map.put("seqno", strAry[0]);
                map.put("title", strAry[1]);
                map.put("cont", strAry[2]);
                map.put("icon", strAry[3]);
                map.put("attachment", strAry[4]);
                map.put("createDate", strAry[5]);
                mapList.add(map);
            }

            SimpleAdapter simpleAdapter = new SimpleAdapter(
                    Popup_notes.this,
                    mapList,
                    R.layout.list_item_notes,
                    new String[] {"title", "cont", "createDate"},
                    new int[] {R.id.textView_title_notes, R.id.textView_des_notes, R.id.textView_create_notes}
            ) {
                @Override
                public View getView (final int position, View convertView, ViewGroup parent) {

                    @SuppressWarnings("unchecked")
                    HashMap<String,String> map = (HashMap<String,String>)listView.getItemAtPosition(position);
                    final String title = map.get("title");
                    final String cont = map.get("cont");
                    final String seqnoStr = map.get("seqno");
                    final String icon = map.get("icon");
                    final String attachment = map.get("attachment");
                    final String create = map.get("createDate");

                    View v = super.getView(position, convertView, parent);
                    ImageView i=(ImageView) v.findViewById(R.id.icon_notes);
                    ImageView i2=(ImageView) v.findViewById(R.id.att_notes);
                    i2.setImageResource(R.drawable.ic_attachment);

                    switch (icon) {
                        case "1":
                            i.setImageResource(R.drawable.circle_green);
                            break;
                        case "2":
                            i.setImageResource(R.drawable.circle_yellow);
                            break;
                        case "3":
                            i.setImageResource(R.drawable.circle_red);
                            break;
                    }
                    switch (attachment) {
                        case "":
                            i2.setVisibility(View.GONE);
                            break;
                        default:
                            i2.setVisibility(View.VISIBLE);
                            i2.setImageResource(R.drawable.ic_attachment);
                            break;
                    }

                    File file = new File(attachment);
                    if (!file.exists()) {
                        i2.setVisibility(View.GONE);
                    }

                    i.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View arg0) {

                            final Item[] items = {
                                    new Item(getString(R.string.note_priority_0), R.drawable.circle_green),
                                    new Item(getString(R.string.note_priority_1), R.drawable.circle_yellow),
                                    new Item(getString(R.string.note_priority_2), R.drawable.circle_red),
                            };

                            ListAdapter adapter = new ArrayAdapter<Item>(
                                    Popup_notes.this,
                                    android.R.layout.select_dialog_item,
                                    android.R.id.text1,
                                    items){
                                @NonNull
                                public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                                    //Use super class to create the View
                                    View v = super.getView(position, convertView, parent);
                                    TextView tv = (TextView)v.findViewById(android.R.id.text1);
                                    tv.setTextSize(18);
                                    tv.setCompoundDrawablesWithIntrinsicBounds(items[position].icon, 0, 0, 0);
                                    //Add margin between image and text (support various screen densities)
                                    int dp5 = (int) (24 * getResources().getDisplayMetrics().density + 0.5f);
                                    tv.setCompoundDrawablePadding(dp5);

                                    return v;
                                }
                            };

                            new AlertDialog.Builder(Popup_notes.this)
                                    .setPositiveButton(R.string.toast_cancel, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            dialog.cancel();
                                        }
                                    })
                                    .setAdapter(adapter, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int item) {
                                            if (item == 0) {
                                                try {
                                                    final Database_Notes db = new Database_Notes(Popup_notes.this);
                                                    db.deleteNote((Integer.parseInt(seqnoStr)));
                                                    db.addBookmark(title, cont, "1", attachment, create);
                                                    db.close();
                                                    setNotesList();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                            } else if (item == 1) {
                                                try {
                                                    final Database_Notes db = new Database_Notes(Popup_notes.this);
                                                    db.deleteNote((Integer.parseInt(seqnoStr)));
                                                    db.addBookmark(title, cont, "2",attachment, create);
                                                    db.close();
                                                    setNotesList();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                            } else if (item == 2) {
                                                try {
                                                    final Database_Notes db = new Database_Notes(Popup_notes.this);
                                                    db.deleteNote((Integer.parseInt(seqnoStr)));
                                                    db.addBookmark(title, cont, "3", attachment, create);
                                                    db.close();
                                                    setNotesList();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }).show();
                        }
                    });
                    i2.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            openAtt(attachment);
                        }
                    });
                    return v;
                }
            };

            listView.setAdapter(simpleAdapter);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static class Item{
        public final String text;
        public final int icon;
        Item(String text, Integer icon) {
            this.text = text;
            this.icon = icon;
        }
        @Override
        public String toString() {
            return text;
        }
    }

    private void openAtt (String fileString) {

        File file = new File(fileString);
        final String fileExtension = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));
        String text = (Popup_notes.this.getString(R.string.toast_extension) + ": " + fileExtension);

        switch (fileExtension) {
            case ".gif":
            case ".bmp":
            case ".tiff":
            case ".svg":
            case ".png":
            case ".jpg":
            case ".jpeg":
                helper_main.openFile(Popup_notes.this, file, "image/*", listView);
                break;
            case ".m3u8":
            case ".mp3":
            case ".wma":
            case ".midi":
            case ".wav":
            case ".aac":
            case ".aif":
            case ".amp3":
            case ".weba":
                helper_main.openFile(Popup_notes.this, file, "audio/*", listView);
                break;
            case ".mpeg":
            case ".mp4":
            case ".ogg":
            case ".webm":
            case ".qt":
            case ".3gp":
            case ".3g2":
            case ".avi":
            case ".f4v":
            case ".flv":
            case ".h261":
            case ".h263":
            case ".h264":
            case ".asf":
            case ".wmv":
                helper_main.openFile(Popup_notes.this, file, "video/*", listView);
                break;
            case ".rtx":
            case ".csv":
            case ".txt":
            case ".vcs":
            case ".vcf":
            case ".css":
            case ".ics":
            case ".conf":
            case ".config":
            case ".java":
                helper_main.openFile(Popup_notes.this, file, "text/*", listView);
                break;
            case ".html":
                helper_main.openFile(Popup_notes.this, file, "text/html", listView);
                break;
            case ".apk":
                helper_main.openFile(Popup_notes.this, file, "application/vnd.android.package-archive", listView);
                break;
            case ".pdf":
                helper_main.openFile(Popup_notes.this, file, "application/pdf", listView);
                break;
            case ".doc":
                helper_main.openFile(Popup_notes.this, file, "application/msword", listView);
                break;
            case ".xls":
                helper_main.openFile(Popup_notes.this, file, "application/vnd.ms-excel", listView);
                break;
            case ".ppt":
                helper_main.openFile(Popup_notes.this, file, "application/vnd.ms-powerpoint", listView);
                break;
            case ".docx":
                helper_main.openFile(Popup_notes.this, file, "application/vnd.openxmlformats-officedocument.wordprocessingml.document", listView);
                break;
            case ".pptx":
                helper_main.openFile(Popup_notes.this, file, "application/vnd.openxmlformats-officedocument.presentationml.presentation", listView);
                break;
            case ".xlsx":
                helper_main.openFile(Popup_notes.this, file, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", listView);
                break;
            case ".odt":
                helper_main.openFile(Popup_notes.this, file, "application/vnd.oasis.opendocument.text", listView);
                break;
            case ".ods":
                helper_main.openFile(Popup_notes.this, file, "application/vnd.oasis.opendocument.spreadsheet", listView);
                break;
            case ".odp":
                helper_main.openFile(Popup_notes.this, file, "application/vnd.oasis.opendocument.presentation", listView);
                break;
            case ".zip":
                helper_main.openFile(Popup_notes.this, file, "application/zip", listView);
                break;
            case ".rar":
                helper_main.openFile(Popup_notes.this, file, "application/x-rar-compressed", listView);
                break;
            case ".epub":
                helper_main.openFile(Popup_notes.this, file, "application/epub+zip", listView);
                break;
            case ".cbz":
                helper_main.openFile(Popup_notes.this, file, "application/x-cbz", listView);
                break;
            case ".cbr":
                helper_main.openFile(Popup_notes.this, file, "application/x-cbr", listView);
                break;
            case ".fb2":
                helper_main.openFile(Popup_notes.this, file, "application/x-fb2", listView);
                break;
            case ".rtf":
                helper_main.openFile(Popup_notes.this, file, "application/rtf", listView);
                break;
            case ".opml":
                helper_main.openFile(Popup_notes.this, file, "application/opml", listView);
                break;

            default:
                Snackbar snackbar = Snackbar
                        .make(listView, text, Snackbar.LENGTH_LONG);
                snackbar.show();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        helper_main.isClosed(Popup_notes.this);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();    //To change body of overridden methods use File | Settings | File Templates.
        helper_main.isOpened(Popup_notes.this);
    }

    @Override
    protected void onResume() {
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
        setNotesList();
        helper_main.isOpened(Popup_notes.this);
    }

    @Override
    protected void onStop() {
        super.onStop();    //To change body of overridden methods use File | Settings | File Templates.
        helper_main.isClosed(Popup_notes.this);
    }
}
