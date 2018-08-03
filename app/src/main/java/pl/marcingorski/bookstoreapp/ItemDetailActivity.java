package pl.marcingorski.bookstoreapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import pl.marcingorski.bookstoreapp.data.BooksContract;
import pl.marcingorski.bookstoreapp.data.BooksContract.BooksEntry;


public class ItemDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_BOOK_LOADER = 0;
    private Uri mCurrentBookUri;

    private TextView mBookName;
    private TextView mBookPrice;
    private TextView mBookQuantity;
    private TextView mBookSupName;
    private TextView mBookSupPhone;

    private Button mMinButton;

    @Override
    protected void onCreate(Bundle savedInstante) {
        super.onCreate ( savedInstante );
        setContentView ( R.layout.item_detail_activity );


        Intent intent = getIntent ();
        mCurrentBookUri = intent.getData ();
        getLoaderManager ().initLoader ( EXISTING_BOOK_LOADER, null, this );

        mBookName = (TextView) findViewById ( R.id.book_name );
        mBookPrice = (TextView) findViewById ( R.id.book_price );
        mBookQuantity = (TextView) findViewById ( R.id.book_quantity );
        mBookSupName = (TextView) findViewById ( R.id.book_sup_name );
        mBookSupPhone = (TextView) findViewById ( R.id.book_sup_phone );

        Button mPlusButton = (Button) findViewById ( R.id.plus_button );
        mMinButton = (Button) findViewById ( R.id.min_button );
        Button mCallSupButton = (Button) findViewById ( R.id.call_sup_button );
        Button mEditButton = (Button) findViewById ( R.id.edit_button );
        Button mDeleteButton = (Button) findViewById ( R.id.delete_button );

        mDeleteButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                // Respond to a click on the "Delete"
                showDeleteConfirmationDialog ();
            }
        } );

        mEditButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent ( ItemDetailActivity.this, EditorActivity.class);
                intent.setData ( mCurrentBookUri );
                startActivity ( intent );
            }
        } );

        mPlusButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               int quantityInteger = Integer.parseInt(mBookQuantity.getText().toString().trim());
                mBookQuantity.setText(String.valueOf(quantityInteger ));
                ContentValues values = new ContentValues (  );
                values.put ( BooksEntry.COLUMN_BOOKS_QUANTITY, quantityInteger +1);
                getContentResolver ().update ( mCurrentBookUri, values,null ,null );

            }
        });

        //Opening phone app, with the Supplier phone number in it, if the user gives us their permission
        mCallSupButton.setOnClickListener( new View.OnClickListener() {

            @Override

            public void onClick(View view) {

                String supPhoneString = mBookSupPhone.getText().toString().trim();

                Intent intent = new Intent(Intent.ACTION_DIAL);

                intent.setData(Uri.parse("tel:" + supPhoneString));

                startActivity(intent);

            }

        });

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                BooksEntry._ID,
                BooksEntry.COLUMN_BOOKS_NAME,
                BooksEntry.COLUMN_BOOKS_PRICE,
                BooksEntry.COLUMN_BOOKS_QUANTITY,
                BooksEntry.COLUMN_BOOKS_SUP_NAME,
                BooksEntry.COLUMN_BOOKS_SUP_PHONE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader ( this,   // Parent activity context
                mCurrentBookUri,         // Query the content URI for the current book
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null );                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader <Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount () < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst ()) {
            // Find the columns of book attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex ( BooksEntry.COLUMN_BOOKS_NAME );
            int priceColumnIndex = cursor.getColumnIndex ( BooksEntry.COLUMN_BOOKS_PRICE );
            int quantityColumnIndex = cursor.getColumnIndex ( BooksEntry.COLUMN_BOOKS_QUANTITY );
            int supNameColumnIndex = cursor.getColumnIndex ( BooksEntry.COLUMN_BOOKS_SUP_NAME );
            int supPhoneColumnIndex = cursor.getColumnIndex ( BooksEntry.COLUMN_BOOKS_SUP_PHONE );


            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString ( nameColumnIndex );
            String price = cursor.getString ( priceColumnIndex );
            int quantity = cursor.getInt ( quantityColumnIndex );
            String supName = cursor.getString ( supNameColumnIndex );
            String supPhone = cursor.getString ( supPhoneColumnIndex );

            // Update the views on the screen with the values from the database
            mBookName.setText ( name );
            mBookPrice.setText ( price );
            mBookQuantity.setText ( Integer.toString ( quantity ) );
            mBookSupName.setText ( supName );
            mBookSupPhone.setText ( supPhone );
        }

    }

    @Override
    public void onLoaderReset(Loader <Cursor> loader) {
        mBookName.setText ( "" );
        mBookPrice.setText ( "" );
        mBookQuantity.setText ( "" );
        mBookSupName.setText ( "" );
        mBookSupPhone.setText ( "" );

    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder ( this );
        builder.setMessage ( R.string.delete_dialog_msg );
        builder.setPositiveButton ( R.string.delete, new DialogInterface.OnClickListener () {
            public void onClick(DialogInterface dialog, int id) {
                deleteBook ();
            }
        } );

        builder.setNegativeButton ( R.string.cancel, new DialogInterface.OnClickListener () {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss ();
                }
            }
        } );

        AlertDialog alertDialog = builder.create ();
        alertDialog.show ();

    }

    private void deleteBook() {
        if (mCurrentBookUri != null) {
            int rowsDeleted = getContentResolver ().delete ( mCurrentBookUri, null, null );

            if (rowsDeleted == 0) {
                Toast.makeText ( this, getString ( R.string.editor_delete_book_failed ),
                        Toast.LENGTH_SHORT ).show ();
            } else {
                Toast.makeText ( this, getString ( R.string.editor_delete_book_successful ), Toast.LENGTH_SHORT ).show ();
            }
        }
        finish ();
    }
}
