package com.example.whowroteit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private EditText mEditText;
    private TextView mResult1;
    private TextView mResult2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEditText = findViewById(R.id.search_text);
        mResult1 = findViewById(R.id.search_result1);
        mResult2 = findViewById(R.id.search_result2);
        if(getSupportLoaderManager().getLoader(0)!=null){
            getSupportLoaderManager().initLoader(0,null,this);
        }
    }

    public void searchBook(View view) {
        String searchText = mEditText.getText().toString();
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
        if (searchText.length() == 0) {
            mResult1.setText("");
            mResult2.setText(R.string.no_search_term);
        }
        else {
            //new FetchBook(mResult1, mResult2).execute(searchText);
            Bundle queryBundle = new Bundle();
            queryBundle.putString("queryString", searchText);
            getSupportLoaderManager().restartLoader(0,queryBundle,this);
        }
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        String queryString = "";

        if (args != null) {
            queryString = args.getString("queryString");
        }
        return new BookLoader(this,queryString);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String s) {
        try{
            JSONObject jsonObject = new JSONObject(s);
            JSONArray itemsArray = jsonObject.getJSONArray("items");

            int i=0;
            String title=null;
            String authors=null;

            while(i<itemsArray.length() && title==null && authors==null)
            {
                JSONObject book = itemsArray.getJSONObject(i);
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");

                try {
                    title = volumeInfo.getString("title");
                    authors = volumeInfo.getString("authors");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                i++;
            }
            if(title!=null && authors!=null)
            {
                mResult1.setText(title);
                mResult2.setText(authors);
            }
            else
            {
                mResult1.setText(R.string.no_results);
                mResult2.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
            mResult1.setText(R.string.no_results);
            mResult2.setText("");
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}
