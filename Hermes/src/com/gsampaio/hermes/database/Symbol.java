package com.gsampaio.hermes.database;

public class Symbol {

	private int id; //ID do símbolo, primary key
	private int board_id; //ID da prancha a que pertence
	private int child_board_id; // ID da prancha que gera (null para símbolo final)
	private String text; // Texto do símbolo
	private String image_path; // Caminho para imagem. Pode ser arquivo ou resource
	private int type; //Tipo (1) Categoria (2) Símbolo final
	private int position; // Posição do símbolo na prancha
	private int page; //Página da prancha em que se encontra
	private int permanent; // 0 ou 1 Se é ou não símbolo permanente (símbolos que já vem no app)
	
	public Symbol(int id, int board_id, int child_board_id, String text, String image_path,
			int type, int position, int page, int permanent) {
		this.id = id;
		this.board_id = board_id;
		this.child_board_id = child_board_id;
		this.text = text;
		this.image_path = image_path;
		this.type = type;
		this.position = position;
		this.page = page;
		this.permanent = permanent;
	}
	
	//Símbolo a ser inserido
	public Symbol(int board_id, int child_board_id, String text, String image_path,
			int type, int position, int page) {
		this.board_id = board_id;
		this.child_board_id = child_board_id;
		this.text = text;
		this.image_path = image_path;
		this.type = type;
		this.position = position;
		this.page = page;
		this.permanent = 0;
	}
	
	//Símbolos vazios
	public Symbol (){
		this.id = -1;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getBoard_id() {
		return board_id;
	}
	public void setBoard_id(int board_id) {
		this.board_id = board_id;
	}
	
	public int getChild_board_id() {
		return child_board_id;
	}

	public void setChild_board_id(int child_board_id) {
		this.child_board_id = child_board_id;
	}

	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getImage_path() {
		return image_path;
	}
	public void setImage_path(String image_path) {
		this.image_path = image_path;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int isPermanent() {
		return permanent;
	}
	public void setPermanent(int permanent) {
		this.permanent = permanent;
	}
	
	
	
}
