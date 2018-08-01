package pl.marcingorski.bookstoreapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import pl.marcingorski.bookstoreapp.data.BooksContract;

public class BookCursorAdapter extends CursorAdapter {

    public BookCursorAdapter(Context context, Cursor c) {super(context, c, 0);}

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the book data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current book can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView summaryTextView = (TextView) view.findViewById(R.id.summary);
        final TextView quantityTextView = (TextView) view.findViewById ( R.id.quantity );

        ImageButton saleButton = view.findViewById ( R.id.sale_button );

        // Find the columns of book attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex( BooksContract.BooksEntry.COLUMN_BOOKS_NAME);
        int priceColumnIndex = cursor.getColumnIndex(BooksContract.BooksEntry.COLUMN_BOOKS_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex ( BooksContract.BooksEntry.COLUMN_BOOKS_QUANTITY );

        // Read the book attributes from the Cursor for the current book
        final String bookName = cursor.getString(nameColumnIndex);
        String bookPrice = cursor.getString(priceColumnIndex);
        if (TextUtils.isEmpty ( bookPrice )) {

            bookPrice = context.getString ( R.string.unknown_price );
        }
        String bookQuantity = cursor.getString ( quantityColumnIndex );

        final int quantity = cursor.getInt(quantityColumnIndex);
        final String id = cursor.getString ( cursor.getColumnIndex ( BooksContract.BooksEntry._ID ) );

        saleButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Uri mCurrentBookUri = Uri.withAppendedPath ( BooksContract.BooksEntry.CONTENT_URI, id );
                ContentValues values = new ContentValues (  );

                if (quantity >= 1) {
                    values.put( BooksContract.BooksEntry.COLUMN_BOOKS_QUANTITY, quantity -1);

                    int rowsAffected = context.getContentResolver ().update ( mCurrentBookUri, values, null, null );

                    Toast.makeText (context, context.getApplicationContext ().getResources ().getString ( R.string.editor_dec_book ), Toast.LENGTH_SHORT).show ();

                } else {
                    Toast.makeText(context,

                            context.getApplicationContext().getResources().getString( R.string.no_book_to_sell ),

                            Toast.LENGTH_SHORT).show();
                }
            }
        } );

        // Update the TextViews with the attributes for the current book
        nameTextView.setText(bookName);
        summaryTextView.setText(bookPrice);
        quantityTextView.setText ( bookQuantity );
    }
}
