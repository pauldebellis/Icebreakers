package com.example.lmont.iceicebb;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;

public class GameDetailActivity extends AppCompatActivity {

    IcebreakerDBHelper dbHelper = IcebreakerDBHelper.getInstance(this);
    Game game;
    String name;
    Context context;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);

        name = getIntent().getStringExtra("name");
        game = dbHelper.getGameWithName(name);
        context = this;

        RelativeLayout titleBar = (RelativeLayout) findViewById(R.id.gameNameLayout);
        TextView gameName = (TextView) findViewById(R.id.gameNameDetail);
        TextView playerCount = (TextView) findViewById(R.id.playerCount);
        ImageView sfwIconAngel = (ImageView) findViewById(R.id.sfwIconAngel);
        ImageView sfwIconDevil = (ImageView) findViewById(R.id.sfwIconDevil);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBarDetail);
        TextView gameMaterials = (TextView) findViewById(R.id.gameMaterials);
        TextView gameRules = (TextView) findViewById(R.id.gameRules);
        TextView gameComments = (TextView) findViewById(R.id.gameComments);
        Button diceRollerButton = (Button) findViewById(R.id.diceRollerButton);
        final Button cardDeckButton = (Button) findViewById(R.id.cardFlipperButton);

        LinearLayout tagHolder = (LinearLayout) findViewById(R.id.tagFrameDetail);
        ImageView drinkIcon = (ImageView) findViewById(R.id.drinkIcon);
        ImageView movingIcon = (ImageView) findViewById(R.id.movingIcon);
        ImageView carIcon = (ImageView) findViewById(R.id.carIcon);
        final ImageView paperIcon = (ImageView) findViewById(R.id.paperIcon);

        float rating = (float) (game.rating) / 2;


        gameName.setText(game.name);
        gameName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameDetailActivity.this, WebViewActivity.class);
                intent.putExtra("VIDEO", game.url);
                intent.putExtra("NAME", game.name);
                startActivity(intent);
            }
        });
        gameMaterials.setText("Required Materials: " + game.materials);
        gameRules.setText("Rules: \n" + game.rules);
        gameComments.setText("Comments\n" + game.comment);
        ratingBar.setRating(rating);
        playerCount.setText(game.minPlayers + "-" + game.maxPlayers);


        if (game.hasCards) {
            cardDeckButton.setVisibility(View.VISIBLE);
        }
        if (game.hasDice) {
            diceRollerButton.setVisibility(View.VISIBLE);
        }

        gameComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openComments();
            }
        });

//ONCLICKS to send user to appropriate tool activity
        diceRollerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DiceActivity.class);
                startActivity(intent);
            }
        });

        cardDeckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CardsActivity.class);
                startActivity(intent);
            }
        });
//IF elses (there's probably a DRYer way) to set SFW icon + visibility of tags
        if (game.isclean) {
            sfwIconAngel.setVisibility(View.VISIBLE);
        }
        if (!game.isclean) {
            sfwIconDevil.setVisibility(View.VISIBLE);
        }
//TODOne: check what tags are present in GAME object and toggle visibility of appropriate tag icons
        if (game.tags.contains("drinking")) {
            drinkIcon.setVisibility(View.VISIBLE);
        }
        if (game.tags.contains("movement")) {
            movingIcon.setVisibility(View.VISIBLE);
        }
        if (game.tags.contains("car")) {
            carIcon.setVisibility(View.VISIBLE);
        }
        if (game.tags.contains("writing")) {
            paperIcon.setVisibility(View.VISIBLE);
        }

        tagHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.drinkIcon:
                        System.out.println("AAAAAAAA TAG ICON WAS CLICKED AAAAAAAAA");
                        Toast drinkToast = Toast.makeText(getApplicationContext(), "This game works well with booze", Toast.LENGTH_SHORT);
                        drinkToast.show();
                        break;
                    case R.id.movingIcon:
                        System.out.println("AAAAAAAA TAG ICON WAS CLICKED AAAAAAAAA");
                        Toast movingToast = Toast.makeText(getApplicationContext(), "This game requires a bit of physical activity", Toast.LENGTH_SHORT);
                        movingToast.show();
                        break;
                    case R.id.carIcon:
                        System.out.println("AAAAAAAA TAG ICON WAS CLICKED AAAAAAAAA");
                        Toast carToast = Toast.makeText(getApplicationContext(), "This is an ideal game to play in a car", Toast.LENGTH_SHORT);
                        carToast.show();
                        break;
                    case R.id.paperIcon:
                        System.out.println("AAAAAAAA TAG ICON WAS CLICKED AAAAAAAAA");
                        Toast paperToast = Toast.makeText(getApplicationContext(), "This game requires a pen and paper", Toast.LENGTH_SHORT);
                        paperToast.show();
                        break;
                }
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void openComments() {
        LayoutInflater inflater = this.getLayoutInflater();
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View dialogView = inflater.inflate(R.layout.comments_dialog, null);
        builder.setView(dialogView);

        final EditText nameEditText = (EditText) dialogView.findViewById(R.id.comments_dialog_name_edittext);
        final EditText reviewEditText = (EditText) dialogView.findViewById(R.id.comments_dialog_review_edittext);
        final RatingBar ratingBar = (RatingBar) dialogView.findViewById(R.id.comments_dialog_ratingbar);
        ListView listView = (ListView) dialogView.findViewById(R.id.comments_dialog_userreviews_listview);

        Game.Comment[] comments = dbHelper.getCommentsForGame(name);
        ArrayAdapter<Game.Comment> adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1);

        for (Game.Comment comment : comments) {
            adapter.add(comment);
        }

        listView.setAdapter(adapter);

        builder.setMessage("Add New Card Game")
                .setPositiveButton("Comment", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (nameEditText.getText().toString().equals("") || reviewEditText.getText().toString().equals("")) {
                            Toast.makeText(context, "Invalid Input", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try {

                            String URL = "https://floating-island-55807.herokuapp.com/games/comments";
                            RequestQueue requestQueue = Volley.newRequestQueue(context);
                            JSONObject jsonBody = new JSONObject();
                            jsonBody.put("gameName", name);
                            jsonBody.put("userName", nameEditText.getText().toString());
                            jsonBody.put("text", reviewEditText.getText().toString());
                            jsonBody.put("rating", ratingBar.getNumStars()*2);

                            final String mRequestBody = jsonBody.toString();

                            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>()
                            {
                                @Override
                                public void onResponse(String response) {
                                    Log.i("VOLLEY", response);
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("VOLLEY", error.toString());
                                }
                            }) {
                                @Override
                                public String getBodyContentType() {
                                    return "application/json; charset=utf-8";
                                }

                                @Override
                                public byte[] getBody() throws AuthFailureError {
                                    try {
                                        return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                                    } catch (Exception uee) {
                                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                                        return null;
                                    }
                                }

                                @Override
                                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                                    String responseString = "";
                                    if (response != null) {
                                        responseString = String.valueOf(response.statusCode);
                                        // can get more details such as response.headers
                                    }
                                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                                }
                            };
                            requestQueue.add(stringRequest);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Cancel
                    }
                });
        builder.show();
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("GameDetail Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
