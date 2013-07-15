package com.gsampaio.hermes.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "hermes.db";
	private static final int DATABASE_VERSION = 1;
	
	private static final int TYPE_CATEGORY = 1;
	
	private static final String TABLE_SYMBOL = "symbols";
	
	//Campos da tabela "symbols"
	private static final String ID = "id";
	private static final String BOARD_ID = "board_id";
	private static final String CHILD_BOARD_ID = "child_board_id";
	private static final String TEXT = "text";
	private static final String IMAGE_PATH = "image_path";
	private static final String TYPE = "type";
	private static final String POSITION = "position";
	private static final String PAGE = "page";
	private static final String PERMANENT = "permanent";
	
	public DBHelper(Context context) {
         super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		String CREATE_SYMBOLS_TABLE = "CREATE TABLE "+TABLE_SYMBOL+
				"("+ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
				BOARD_ID+" INTEGER,"+
				CHILD_BOARD_ID+" INTEGER,"+
				TEXT+" TEXT,"+
				IMAGE_PATH+" TEXT,"+
				TYPE+" INTEGER,"+
				POSITION+" INTEGER,"+
				PAGE+ " INTEGER,"+
				PERMANENT+" BOOLEAN);";
        db.execSQL(CREATE_SYMBOLS_TABLE);
        populate(db);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	       db.execSQL("DROP TABLE IF EXISTS " + TABLE_SYMBOL);
	       onCreate(db);
	}
	
	public long addSymbol(Symbol symbol){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(BOARD_ID, symbol.getBoard_id());
		values.put(TEXT, symbol.getText());
		values.put(IMAGE_PATH, symbol.getImage_path());
		values.put(TYPE, symbol.getType());
		values.put(POSITION, symbol.getPosition());
		values.put(PAGE, symbol.getPage());
		values.put(PERMANENT, symbol.isPermanent());
		if(symbol.getType() != TYPE_CATEGORY){
			values.put(CHILD_BOARD_ID, -1); //Se não é categoria, não possui child board
		}else{
			values.put(CHILD_BOARD_ID, symbol.getChild_board_id());
		}
		
		long id = db.insert(TABLE_SYMBOL, null, values);
		db.close();
		return id;
	}
	
	public Symbol getSymbol(int board_id, int page, int position){
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_SYMBOL, 
				new String[] {ID, BOARD_ID, CHILD_BOARD_ID, TEXT, IMAGE_PATH, TYPE, POSITION, PAGE, PERMANENT},
				BOARD_ID + "=? AND "+PAGE+"=? AND "+POSITION+"=?",
				new String[] {String.valueOf(board_id), String.valueOf(page), String.valueOf(position)}, null, null, null, null);
		Symbol symbol;
		if (cursor != null){
            if(cursor.moveToFirst()){
	            symbol = new Symbol(	Integer.parseInt(cursor.getString(0)), 
            		Integer.parseInt(cursor.getString(1)), 
    				Integer.parseInt(cursor.getString(2)), 
					cursor.getString(3), 
					cursor.getString(4), 
					Integer.parseInt(cursor.getString(5)), 
					Integer.parseInt(cursor.getString(6)), 
					Integer.parseInt(cursor.getString(7)), 
					Integer.parseInt(cursor.getString(8)));
	            db.close();
	            return symbol;
			}
		}
		symbol = new Symbol();
		db.close();
		return symbol;	
	}
	
	public Symbol getSymbolById(int id){
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_SYMBOL, 
				new String[] {ID, BOARD_ID, CHILD_BOARD_ID, TEXT, IMAGE_PATH, TYPE, POSITION, PAGE, PERMANENT},
				ID + "=?", 
				new String[] {String.valueOf(id)}, null, null, null, null);
		Symbol symbol;
		if (cursor != null){
            if(cursor.moveToFirst()){
	            symbol = new Symbol(	Integer.parseInt(cursor.getString(0)), 
            		Integer.parseInt(cursor.getString(1)), 
    				Integer.parseInt(cursor.getString(2)), 
					cursor.getString(3), 
					cursor.getString(4), 
					Integer.parseInt(cursor.getString(5)), 
					Integer.parseInt(cursor.getString(6)), 
					Integer.parseInt(cursor.getString(7)), 
					Integer.parseInt(cursor.getString(8)));
	            db.close();
	            return symbol;
			}
		}
		symbol = new Symbol();
		db.close();
		return symbol;
	}
	
	public void deleteSymbol (Symbol symbol){
		
	}
	
	public void populate(SQLiteDatabase db){
		
		//Board 0, Pagina 1
		db.insert(TABLE_SYMBOL, null, getContentValues(
				0, 1, 0, 0, 1, "quero", "Eu quero"));
		db.insert(TABLE_SYMBOL, null, getContentValues(
				0, 2, 2, 4 , 2, "quero", "Eu quero"));
	}
	
	public ContentValues getContentValues(int board_id, int child_board_id, int page, int position, int type, String image_path, String text){
		ContentValues values = new ContentValues();
		
		values.put(BOARD_ID, board_id);
		values.put(TEXT, text);
		values.put(IMAGE_PATH, image_path);
		values.put(TYPE, type);
		values.put(POSITION, position);
		values.put(PAGE, page);
		values.put(PERMANENT, true);
		if(type != TYPE_CATEGORY){
			values.put(CHILD_BOARD_ID, -1); //Se não é categoria, não possui child board
		}else{
			values.put(CHILD_BOARD_ID, child_board_id);
		}
			return values;
	}

}
