/* Java сервлет классической игры Судоку
 * Каждая задача судоку при генерации решается естественным алгоритмом (SudokuSolver). 
 * Алгоритм проверяет задачу на наличие только одного логического решения
 * и возвращает ее в основной класс (Sudoku) для отображения на HTML странице.   
 * */

package sudoku_2015;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

 
@WebServlet("/sudoku")    
public class Sudoku extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static int[][] bigGameArray;                            // основной массив чисел, содержащий все ответы
	private HttpServletResponse httpResponse;  
	private String htmlMenuCode;                            // строка содержит HTML код, отображающий 9 кнопок вариантов ответа
	private String firstParameterName = "z";                // 1 параметр содержит уровень сложности, инициируется неиспользуемой буквой z 
	private String secondParameterName = "z";               // 2 параметр содержит последнюю нажатую кнопку
	private String firstParameterValue;                     
	private String secondParameterValue;
	private Enumeration<?> paramNamesEnumeration;           // используется для извлечения имен параметров
	static int gameStatus = 0;                              // 0 - режим ожидания (чистое поле без нажатых кнопок)
	// 1 - ожидается нажатие кнопки на рабочем поле(9х9), 2 - нажата кнопка на рабочем поле, ожидается нажатие кнопки меню (выбора значения)
	
	private int lastButtonNum;                              // номер последней нажатой кнопки
	static  String message = "";                            // используется для вывода сообщений на HTML странице
	static SudokuGameButton[][] arrayOfButtons = new SudokuGameButton[9][9];    // массив экземпляров кнопок
	static int gameComplexity = 0;                          // сложность игры, от 0 до 3
	static boolean[][] openButtonsArray = new boolean [9][9];   // массив открытых кнопок, создается генератором каждый раз отдельно
	static boolean testMode = true;                         // для отображения тех. информации назначить true

   
    public Sudoku() throws IOException, ServletException {
    	try {
    		startGame();
    		}
    	catch (Exception e) {
    		throw new ServletException("Sudoku constructor exception.\n" + displayErrorForWeb(e));   
    		// все ошибки конструктора выводятся в HTML  
    	}     	
    }    
     
    private void startGame() {
    	SudokuGenerator sGenerator = new SudokuGenerator();    	
    	bigGameArray = sGenerator.generateSudokuArray();     // генерируем рабочий массив, 81 число
    	openButtonsArray = sGenerator.getOpenButtonsArray(); 
    	// генерируем массив открытых кнопок, который вместе с рабочим массивом уже проверен и имеет единственное решение	
    	createButtonMenuCode();                              
    	setButtonArray();
    	SudokuGameButton.initiateButtonsArray(bigGameArray);  // инициируем экземпляры кнопок рабочего поля
    	updateAllButtonsCode();    	
    }
    
    private void setButtonArray(){            // генерирует массив экземпляров кнопок                     
		for (int i = 0; i<9;i++ ){
    		for (int j = 0; j<9; j++){
    			arrayOfButtons[i][j] = new SudokuGameButton(i,j);	
    		}
    	}
	}
    
    private void updateAllButtonsCode(){                     
    	// каждая кнопка генерирует свой HTML код, который впоследствии собирается в код рабочего поля 
		for (int i = 0; i<9;i++ ){
    		for (int j = 0; j<9; j++){	
    			arrayOfButtons[i][j].updateButtonHtmlCode();
    		}
    	}
	}
    
    static void printHtml(String inStr){           // выводит текст на HTML странице  
    	if (testMode == true){
    		message += "&nbsp;" + inStr;
		} 	
    } 
    
    private void createButtonMenuCode(){             // создает HTML код меню (9 кнопок выбора значений)
    	htmlMenuCode = ""; 
    	for (int i = 0; i<9;i++ ){
    		htmlMenuCode += "<th><input class=\"buttdrk\" name=\"m0" + (i+1) +"\" type=\"submit\" value=\"" + (i+1) +"\"/></th>";    			
    		}
    	htmlMenuCode = "<br><br><br><table class=\"center\"><tr>" + htmlMenuCode + "</tr></table>";    	
    }
     
    private void getRequestParameters(HttpServletRequest httpReq){   
    	// извлекает имена и значения 2 параметров http запроса
    	paramNamesEnumeration = httpReq.getParameterNames();
	    if (paramNamesEnumeration.hasMoreElements()) {
	    	firstParameterName = (String) paramNamesEnumeration.nextElement();
	    	secondParameterName = (String) paramNamesEnumeration.nextElement();
	    	}
	    Iterator<String[]> reqIter = httpReq.getParameterMap().values().iterator();
	    if (reqIter.hasNext()) {
	    	firstParameterValue = reqIter.next()[0];
	    	secondParameterValue = reqIter.next()[0];
	    	}  
	    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// при каждом нажатии на кнопку или обновлении страницы
		try {
			httpResponse = response;
			PrintWriter pw = response.getWriter();     // готовим HTML ответ клиенту
			response.setContentType("text/html");
			getRequestParameters(request);
			if (secondParameterName.charAt(0) == "n".charAt(0)) {     // нажата кнопка New game 
				changeComplexity();
			}
			if (gameStatus == 2) {         // нажата кнопка меню выбора значения
				prepareForValueChange();				
			}
			if (gameStatus == 1) {         // нажата кнопка рабочего поля
				prepareForColorChange();				
			} else {
				gameStatus = 0;
			}

			if (gameStatus == 0){          // режим ожидания (чистое поле без нажатых кнопок)
				pw.println(getHtmlHead() + getHtmlBody());
				gameStatus = 1;
			}
			
			pw.close();
		} catch (Exception e) {
			throw new ServletException("Sudoku doGet exception.\n" + displayErrorForWeb(e));
			// все ошибки выводятся в HTML ответе 
		}
	}
	
	private void changeComplexity(){                  // меняет сложность и перезапускает игру
		switch (firstParameterValue.charAt(0)) {
		case 'E':
			gameComplexity = 0;			
			break;
		case 'N':
			gameComplexity = 1;
			break;
		case 'H':
			gameComplexity = 2;
			break;}				
		gameStatus = 0;
		startGame();
	}
	
	private void prepareForValueChange(){                         // меняет значение кнопки рабочего поля
		if (secondParameterName.charAt(0) == "m".charAt(0)) {     // если нажата кнопка меню выбора значений
			int lastValue = Integer.parseInt(secondParameterValue);
			SudokuGameButton.changeHtmlFieldArray(lastButtonNum,lastValue);   // попытка изменить значение
			gameStatus = 0; 
		} else if (secondParameterName.charAt(0) == "b".charAt(0)) { // если нажата другая кнопка рабочего поля
			gameStatus = 1;
		}
	}
	
	private void prepareForColorChange() throws IOException{    // меняет цвет кнопки рабочего поля
		if (secondParameterName.charAt(0) == "b".charAt(0)){
			lastButtonNum = Integer.parseInt(secondParameterName.substring(1, 3));   
			// запоминает номер нажатой кнопки
			SudokuGameButton.changeColor(lastButtonNum);
			httpResponse.getWriter().println(getHtmlHead() + getHtmlBody());
			gameStatus = 2;	                                   
		}
		else {
			gameStatus = 0;
		}
	}
 	
	private String getHtmlHead(){ 		// HTML заголовок для каждой страницы, содержащий все CSS стили
		return "<!doctype html><html><head><title>Sudoku</title>"+
				"<style> .butt{border: 1px outset #afafaf;background-color:#ffcf77;height:30px;width:30px;cursor:pointer;}"+
				".butt:hover{background-color:#e4b96a;} .buttdrk{border:1px outset #afafaf;background-color:#7accc8;height:30px;width:30px;cursor:pointer;}"+ 
				".buttdrk:hover{background-color:#6bb4b0;} .bdrk{border:1px outset #afafaf;background-color:#f2ea90;height:30px;width:30px;cursor:pointer;}"+
				".bdrk:hover{background-color:#c9c378;} table.center{margin-left:auto;margin-right:auto;border-spacing: 0px;}body{background-color:#e1e1e1;}"+				
				"</style></head><body><br>";
	}
	
	private String getHtmlBody(){	  // генерирует оставшийся HTML код 	
		String htmlCenterCode = "<form name=\"Form1\" action=\"sudoku\">";
		htmlCenterCode +=  "<div align=\"right\">Level: <select name=\"level\"><option>Easy</option><option>Normal</option><option>Hard</option></select>&nbsp;<input name=\"n40\" type=\"submit\" value=\"New game\"/></div><br><br>";
		// код кнопки начала новой игры
		htmlCenterCode += SudokuGameButton.createStringFromAllButtonsCode();   // генерирует и добавляет HTML код рабочего поля
		if (gameStatus==1){                                                   
			htmlCenterCode += htmlMenuCode;                                   // добавляет HTML код меню выбора значений
			}	
		
		htmlCenterCode += "</form><br><br>" + message + "</body></html>";     // выводит сообщения от разработчика
		message = "";
		return htmlCenterCode;  
	}
	
	String displayErrorForWeb(Throwable t) {            // получает StackTrace и приводит его к наглядному виду
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		String stackTrace = sw.toString();
		stackTrace = "\n" + stackTrace;
		return stackTrace.replace(System.getProperty("line.separator"), "\n");
	}
	
	static void printIntArray(int[][] incomeArr) {   // выводит массив чисел на HTML странице
		if (testMode == false){
			return;
		}
		for (int i = 0; i < incomeArr.length; i++) {
			printHtml("<br>");
			for (int j = 0; j < incomeArr.length; j++) {
				if (incomeArr[i][j]>0){
				printHtml(incomeArr[i][j] + "");}
				else {printHtml("&nbsp;&nbsp;"); }
			}
		}
	}
}