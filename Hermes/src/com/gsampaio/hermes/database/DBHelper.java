package com.gsampaio.hermes.database;


import java.io.File;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
				PERMANENT+" INTEGER);";
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
	
	public int getNextBoardId(){
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_SYMBOL, 
				new String[] {"MAX("+ID+")"}, null, null, null, null, null, null);
		cursor.moveToFirst();
		db.close();
		return Integer.parseInt(cursor.getString(0))+1;
	}
	
	public boolean deleteCategorySymbol (int symbol_id){
		Symbol symbol_del = getSymbolById(symbol_id);
		if(symbol_del.isPermanent()!=1){
			SQLiteDatabase db = this.getWritableDatabase();
			Cursor cursor = db.query(TABLE_SYMBOL, new String[]{ID},BOARD_ID+"=?" , new String[]{String.valueOf(symbol_del.getChild_board_id())}, null, null, null);
			db.delete(TABLE_SYMBOL, ID+"=?", new String[] {String.valueOf(symbol_id)});
			File image_del = new File(symbol_del.getImage_path());
			image_del.delete();
            
			if (cursor!= null){
				if(cursor.moveToFirst()){
					do{
						int child_id = Integer.parseInt(cursor.getString(0));
						if(symbol_del.getType()==1){
							deleteCategorySymbol(child_id);
						}else{
							deleteFinalSymbol(child_id);
						}
					}while(cursor.moveToNext());
				}
			}
			db.close();
			return true;
		}
		return false;
	}
	
	public boolean deleteFinalSymbol(int symbol_id){
		Symbol symbol_del = getSymbolById(symbol_id);
		if(symbol_del.isPermanent()!=1){
			SQLiteDatabase db = this.getWritableDatabase();
			db.delete(TABLE_SYMBOL, ID+"=?", new String[] {String.valueOf(symbol_id)});
			File image_del = new File(symbol_del.getImage_path());
			image_del.delete();
			db.close();
			return true;
		}
		return false;
	}
	
	public void populate(SQLiteDatabase db){
		
		//Board 0, Pagina 1
		db.insert(TABLE_SYMBOL, null, getContentValues(	0, -1, 0, 0, 0, "sim", "sim"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	0, 1, 0, 1, 1, "eu", "eu"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	0, -1, 0, 2, 0, "oi", "oi"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	0, -1, 0, 3, 0, "nao", "não"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	0, -1, 0, 4, 0, "tchau", "adeus"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	0, -1, 0, 5, 0, "desculpe", "desculpe"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	0, -1, 0, 6, 0, "porfavor", "por favor"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	0, -1, 0, 7, 0, "meajude", "meajude"));
		
		//Board 0, Pagina 2
		db.insert(TABLE_SYMBOL, null, getContentValues(	0, 2, 1, 0, 1, "pai", "meu pai"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	0, 2, 1, 1, 1, "mae", "minha mae"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	0, 3, 1, 2, 1, "amigo", "meu amigo"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	0, 3, 1, 3, 1, "amiga", "minha amiga"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	0, 3, 1, 4, 1, "irma", "minha irmã"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	0, 3, 1, 5, 1, "irmao", "meu irmão"));
		
		//Board 0, Pagina 3
		db.insert(TABLE_SYMBOL, null, getContentValues(	0, -1, 2, 0, 0, "ontem", "ontem"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	0, -1, 2, 1, 0, "hoje", "hoje"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	0, -1, 2, 2, 0, "amanha", "amanhã"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	0, -1, 2, 3, 0, "agora", "agora"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	0, 4, 2, 4, 1, "meses", ""));
		db.insert(TABLE_SYMBOL, null, getContentValues(	0, 5, 2, 5, 1, "dias", ""));
		
		//Board 1 - Eu, Pagina 1
		db.insert(TABLE_SYMBOL, null, getContentValues(	1, 6, 0, 0, 1, "quero", "quero"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	1, 7, 0, 1, 1, "sou", "sou"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	1, 8, 0, 2, 1, "estou", "estou"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	1, 9, 0, 3, 1, "sinto", "sinto"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	1, 10, 0, 4, 1, "preciso", "preciso"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	1, 11, 0, 5, 1, "gosto", "gosto"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	1, -1, 0, 6, 0, "nao", "não"));
		
		//Board 1 - Eu, Pagina 2
		db.insert(TABLE_SYMBOL, null, getContentValues(	1, -1, 1, 0, 0, "teamo", "te amo"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	1, -1, 1, 1, 0, "acredito", "acredito"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	1, -1, 1, 2, 0, "confio", "confio"));
		
		//Board 2 - Pais, Pagina 1
		db.insert(TABLE_SYMBOL, null, getContentValues(	2, -1, 0, 0, 0, "elogiar", "me elogiou"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	2, 12, 0, 1, 1, "ensinou", "me ensinou"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	2, -1, 0, 2, 0, "teamo", "me ama"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	2, -1, 0, 3, 0, "mebateu", "me bateu"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	2, -1, 0, 4, 0, "contar", "me contou"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	2, -1, 0, 5, 0, "brigou", "brigou"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	2, -1, 0, 6, 0, "mandou", "mandou"));
		
		//Board 3 - Amigos, Irmãos, Pagina 1
		db.insert(TABLE_SYMBOL, null, getContentValues(	3, -1, 0, 0, 0, "brincar", "brincou"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	3, -1, 0, 1, 0, "brigou", "brigou"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	3, -1, 0, 2, 0, "mebateu", "me bateu"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	3, -1, 0, 3, 0, "abracou", "me abraçou"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	3, -1, 0, 4, 0, "contar", "me contou"));
		
		//Board 4 - Meses
		db.insert(TABLE_SYMBOL, null, getContentValues(	4, -1, 0, 0, 0, "janeiro", "janeiro"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	4, -1, 0, 1, 0, "fevereiro", "fevereiro"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	4, -1, 0, 2, 0, "marco", "março"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	4, -1, 0, 3, 0, "abril", "abril"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	4, -1, 0, 4, 0, "maio", "maio"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	4, -1, 0, 5, 0, "junho", "junho"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	4, -1, 0, 6, 0, "julho", "julho"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	4, -1, 0, 7, 0, "agosto", "agosto"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	4, -1, 1, 0, 0, "setembro", "setembro"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	4, -1, 1, 1, 0, "outubro", "outubro"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	4, -1, 1, 2, 0, "novembro", "novembro"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	4, -1, 1, 3, 0, "dezembro", "dezembro"));
		
		//Board 5 - Dias
		db.insert(TABLE_SYMBOL, null, getContentValues(	5, -1, 0, 0, 0, "segundafeira", "segunda"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	5, -1, 0, 1, 0, "tercafeira", "terça"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	5, -1, 0, 2, 0, "quartafeira", "quarta"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	5, -1, 0, 3, 0, "quintafeira", "quinta"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	5, -1, 0, 4, 0, "sextafeira", "sexta"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	5, -1, 0, 5, 0, "sabado", "sábado"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	5, -1, 0, 6, 0, "domingo", "domingo"));
		
		//Board 6 - Quero, Pagina 1
		db.insert(TABLE_SYMBOL, null, getContentValues(	6, -1, 0, 0, 0, "dormir", "dormir"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	6, 13, 0, 1, 1, "comer", "comer"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	6, 14, 0, 2, 1, "beber", "beber"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	6, 15, 0, 3, 1, "ir", "ir"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	6, 16, 0, 4, 1, "comprar", "comprar"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	6, 17, 0, 5, 1, "estudar", "estudar"));
		
		//Board 6 - Quero, Pagina 2
		db.insert(TABLE_SYMBOL, null, getContentValues(	6, 18, 1, 0, 1, "fazer", "fazer"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	6, -1, 1, 1, 0, "chorar", "chorar"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	6, -1, 1, 2, 0, "cantar", "cantar"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	6, -1, 1, 3, 0, "pintar", "pintar"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	6, 19, 1, 4, 1, "brincar", "brincar"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	6, -1, 1, 5, 0, "trabalhar", "trabalhar"));
		
		//Board 6 - Quero, Pagina 3
		db.insert(TABLE_SYMBOL, null, getContentValues(	6, -1, 2, 0, 0, "sair", "sair"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	6, -1, 2, 1, 0, "chegar", "chegar"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	6, -1, 2, 2, 0, "contar", "contar"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	6, -1, 2, 3, 0, "falar", "falar"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	6, 20, 2, 4, 1, "outracoisa", ""));
		
		//Board 7 - Sou, Pagina 1
		db.insert(TABLE_SYMBOL, null, getContentValues(	7, -1, 0, 0, 0, "feliz", "feliz"));
		
		//Board 8 - Estou, Pagina 1
		db.insert(TABLE_SYMBOL, null, getContentValues(	8, -1, 0, 0, 0, "feliz", "feliz"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	8, -1, 0, 1, 0, "triste", "triste"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	8, -1, 0, 2, 0, "alegre", "alegre"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	8, -1, 0, 3, 0, "irritado", "irritado"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	8, -1, 0, 4, 0, "envergonhado", "envergonhado"));
		
		//Board 8 - Estou, Pagina 2
		db.insert(TABLE_SYMBOL, null, getContentValues(	8, -1, 1, 0, 0, "ouvindo", "ouvindo"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	8, -1, 1, 1, 0, "falar", "falando"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	8, -1, 1, 2, 0, "ver", "vendo"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	8, -1, 1, 3, 0, "pronto", "pronto"));
		
		//Board 9 - Sinto, Pagina 1
		db.insert(TABLE_SYMBOL, null, getContentValues(	9, -1, 0, 0, 0, "frio", "frio"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	9, -1, 0, 1, 0, "calor", "calor"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	9, -1, 0, 2, 0, "fome", "fome"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	9, -1, 0, 3, 0, "medo", "medo"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	9, -1, 0, 4, 0, "envergonhado", "vergonha"));
		
		//Board 10 - Preciso, Pagina 1
		db.insert(TABLE_SYMBOL, null, getContentValues(	10, 15, 0, 0, 1, "ir", "ir"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	10, -1, 0, 1, 0, "ajuda", "de ajuda"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	10, -1, 0, 2, 0, "voce", "de você"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	10, -1, 0, 3, 0, "carinho", "de carinho"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	10, -1, 0, 4, 0, "atencao", "de atenção"));
		
		//Board 11 - Gosto, Pagina 1
		db.insert(TABLE_SYMBOL, null, getContentValues(	11, -1, 0, 0, 0, "voce", "de você"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	11, 13, 0, 1, 1, "comer", "de comer"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	11, 15, 0, 2, 1, "ir", "de ir"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	11, -1, 0, 3, 0, "cantar", "de cantar"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	11, -1, 0, 4, 0, "dancar", "de dançar"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	11, 19, 0, 5, 1, "brincar", "de brincar"));
		
		//Board 13 - Comer, Pagina 1
		db.insert(TABLE_SYMBOL, null, getContentValues(	13, -1, 0, 0, 0, "pao", "pão"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	13, -1, 0, 1, 0, "queijo", "queijo"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	13, -1, 0, 2, 0, "presunto", "presunto"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	13, -1, 0, 3, 0, "manteiga", "manteiga"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	13, -1, 0, 4, 0, "ovos", "ovos"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	13, -1, 0, 5, 0, "cereal", "cereal"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	13, 21, 0, 6, 1, "fruta", ""));
		
		//Board 13 - Comer, Pagina 2
		db.insert(TABLE_SYMBOL, null, getContentValues(	13, -1, 1, 0, 0, "arroz", "arroz"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	13, -1, 1, 1, 0, "macarrao", "macarrão"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	13, -1, 1, 2, 0, "feijao", "feijão"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	13, 22, 1, 3, 1, "salada", "salada"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	13, -1, 1, 4, 0, "carne", "carne"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	13, -1, 1, 5, 0, "frango", "frango"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	13, -1, 1, 6, 0, "peixe", "peixe"));
		
		//Board 13 - Comer, Pagina 3
		db.insert(TABLE_SYMBOL, null, getContentValues(	13, -1, 2, 0, 0, "biscoito", "biscoito"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	13, -1, 2, 1, 0, "bolo", "bolo"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	13, -1, 2, 2, 0, "sopa", "sopa"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	13, -1, 2, 3, 0, "mingau", "mingau"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	13, 23, 2, 4, 1, "outracoisa", ""));
	
		//Board 14 - Beber, Pagina 1
		db.insert(TABLE_SYMBOL, null, getContentValues(	14, -1, 0, 0, 0, "agua", "água"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	14, 21, 0, 1, 1, "suco", "suco de"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	14, -1, 0, 2, 0, "leite", "leite"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	14, -1, 0, 3, 0, "cafe", "café"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	14, -1, 0, 4, 0, "achocolatado", "achocolatado"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	14, -1, 0, 5, 0, "cha", "chá"));
		
		//Board 15 - Ir, Pagina 1
		db.insert(TABLE_SYMBOL, null, getContentValues(	15, 24, 0, 0, 1, "banheiro", "ao banheiro"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	15, -1, 0, 1, 0, "quarto", "ao quarto"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	15, -1, 0, 2, 0, "sala", "à sala"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	15, -1, 0, 3, 0, "cozinha", "à cozinha"));
		
		//Board 15 - Ir, Pagina 2
		db.insert(TABLE_SYMBOL, null, getContentValues(	15, -1, 1, 0, 0, "parque", "ao parque"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	15, -1, 1, 1, 0, "escola", "à escola"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	15, -1, 1, 2, 0, "shopping", "ao shopping"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	15, -1, 1, 3, 0, "cinema", "ao cinema"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	15, -1, 1, 4, 0, "biblioteca", "à biblioteca"));
		
		//Board 17 - Estudar, Pagina 1
		db.insert(TABLE_SYMBOL, null, getContentValues(	17, -1, 0, 0, 0, "matematica", "matemática"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	17, -1, 0, 1, 0, "portugues", "português"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	17, -1, 0, 2, 0, "historia", "história"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	17, -1, 0, 3, 0, "geografia", "geografia"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	17, -1, 0, 4, 0, "ciencias", "ciências"));
		
		//Board 20 - Outra coisa (Quero), Pagina 1
		db.insert(TABLE_SYMBOL, null, getContentValues(	20, -1, 0, 0, 0, "ler", "ler"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	20, -1, 0, 1, 0, "vertv", "ver televisão"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	20, -1, 0, 2, 0, "bicicleta", "andar de bicicleta"));
		
		//Board 21 - Fruta, Pagina 1
		db.insert(TABLE_SYMBOL, null, getContentValues(	21, -1, 0, 0, 0, "maca", "maçã"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	21, -1, 0, 1, 0, "banana", "banana"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	21, -1, 0, 2, 0, "melancia", "melancia"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	21, -1, 0, 3, 0, "pera", "pêra"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	21, -1, 0, 4, 0, "uva", "uva"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	21, -1, 0, 5, 0, "melao", "melão"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	21, -1, 0, 6, 0, "mamao", "mamão"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	21, -1, 0, 7, 0, "abacaxi", "abacaxi"));
		
		//Board 21 - Fruta, Pagina 2
		db.insert(TABLE_SYMBOL, null, getContentValues(	21, -1, 1, 0, 0, "laranja", "laranja"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	21, -1, 1, 1, 0, "tangerina", "tangerina"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	21, -1, 1, 2, 0, "morango", "morango"));
		
		//Board 22 - Salada, Pagina 1
		db.insert(TABLE_SYMBOL, null, getContentValues(	22, -1, 0, 0, 0, "tomate", "tomate"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	22, -1, 0, 1, 0, "alface", "alface"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	22, -1, 0, 2, 0, "cenoura", "cenoura"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	22, -1, 0, 3, 0, "beterraba", "beterraba"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	22, -1, 0, 4, 0, "batata", "batata"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	22, -1, 0, 5, 0, "couve", "couve"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	22, -1, 0, 6, 0, "brocolis", "brócolis"));
		
		//Board 24 - Banheiro, Pagina 1
		db.insert(TABLE_SYMBOL, null, getContentValues(	24, -1, 0, 0, 0, "tomarbanho", "tomar banho"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	24, -1, 0, 1, 0, "sanitario", "usar o sanitário"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	24, -1, 0, 2, 0, "lavarasmaos", "lavar as mãos"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	24, -1, 0, 3, 0, "lavarorosto", "lavar o rosto"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	24, -1, 0, 4, 0, "escovarosdentes", "escovar os dentes"));
		db.insert(TABLE_SYMBOL, null, getContentValues(	24, -1, 0, 5, 0, "assoar", "assoar o nariz"));
		
	}
	
	public ContentValues getContentValues(int board_id, int child_board_id, int page, int position, int type, String image_path, String text){
		ContentValues values = new ContentValues();
		
		values.put(BOARD_ID, board_id);
		values.put(TEXT, text);
		values.put(IMAGE_PATH, image_path);
		values.put(TYPE, type);
		values.put(POSITION, position);
		values.put(PAGE, page);
		values.put(PERMANENT, 1);
		if(type != TYPE_CATEGORY){
			values.put(CHILD_BOARD_ID, -1); //Se não é categoria, não possui child board
		}else{
			values.put(CHILD_BOARD_ID, child_board_id);
		}
			return values;
	}

}
