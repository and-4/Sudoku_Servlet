// Класс содержит данные каждой рабочей кнопки и логику, генерирующую HTML код для каждой кнопки 

package sudoku_2015;

class SudokuGameButton {
	static private int previousColoredCellNum = 100;              // номер последней выделенной (раскрашенной) кнопки
	static SudokuGameButton[][] arrayOfButtons = Sudoku.arrayOfButtons;

	private String buttType;     // тип кнопки, "submit" - кнопка активна (вызывает doGet), "button" - кнопка пассивна
	private String buttValue;    // отображает численное значение на самой кнопке, если значение есть - кнопка пассивна
	private String buttClass;    // стиль кнопки, "butt" - желтая кнопка, "bdrk" - темно желтая,  "buttdrk" - синяя (нажатая кнопка и меню выбора) 
	private String buttCode;     // содержит HTML код самой кнопки
	private int butStrNum;       // номер строки кнопки 
	private int butRowNum;       // номер ряда(столбца) кнопки
	private boolean isOpen = false;   // true - кнопка открыта, false - не разгаданая кнопка 
	

	SudokuGameButton(int inButStrNum, int inButRowNum) {
		butStrNum = inButStrNum;
		butRowNum = inButRowNum;
	}

	static void changeHtmlFieldArray(int numOfCell, int newValue) {      // пробуем открыть кнопку 
		int strNum = numOfCell / 10;
		int rowNum = numOfCell % 10;
		if (Sudoku.bigGameArray[strNum][rowNum] == newValue) {           // если правильное значение 
			arrayOfButtons[strNum][rowNum].isOpen = true;
			SetButtValue(strNum, rowNum, newValue);						 // открываем кнопку
		} else {                                                         // если ошиблись
			previousColoredCellNum = 100;
			Sudoku.message += "<center>Wrong number!</center>";          // выводим сообщение об ошибке
		}
		arrayOfButtons[strNum][rowNum].updateButtClass();
		arrayOfButtons[strNum][rowNum].updateButtType();
		arrayOfButtons[strNum][rowNum].updateButtonHtmlCode();
	}


	static void initiateButtonsArray(int[][] incomeValueArray) {          // инициируем значения всех рабочих кнопок
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				arrayOfButtons[i][j].buttValue = incomeValueArray[i][j]+ "";
			}
		}
		initiateAllButtonsState();
	}

	void updateButtonHtmlCode() {                                           
		// каждая кнопка по необходимости генерирует свой HTML код
		buttCode = "<th><input class=\"" + buttClass + "\" name=\"b" + butStrNum + butRowNum + "\" type=\""
				+ buttType + "\" value=\"" + buttValue + "\"/></th>";
		if (butRowNum == 0) {                 // добавляет знак начало строки таблицы
			buttCode = "<tr>" + buttCode;
		} else if (butRowNum == 8) {          // или конец строки
			buttCode += "</tr>";
		}
	}

	static String createStringFromAllButtonsCode() {     // генерирует единую HTML строку рабочего поля
		String buttonHtmlCode = "";
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				buttonHtmlCode += arrayOfButtons[i][j].buttCode;
			}
		}
		buttonHtmlCode = "<table class=\"center\">" + buttonHtmlCode + "</table>";
		return buttonHtmlCode;
	}

	static void initiateAllButtonsState() {          // инициирует состояние всех кнопок
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				arrayOfButtons[i][j].getButtOpenStatus();
				arrayOfButtons[i][j].updateButtType();
				arrayOfButtons[i][j].getButtValue();
				arrayOfButtons[i][j].updateButtClass();
			}
		}
	}

	static void changeColor(int numOfCell) {            // меняет цвет текущей и предыдущей нажатой кнопки 
		if (numOfCell == previousColoredCellNum) {      // кнопка нажата второй раз
			return;
		}
		int strNum = numOfCell / 10;
		int rowNum = numOfCell % 10;
		arrayOfButtons[strNum][rowNum].updateButtClass();        // меняем цвет текущей кнопки на синий
		arrayOfButtons[strNum][rowNum].updateButtonHtmlCode();
		if (previousColoredCellNum < 100) {                            // если имеется предыдущая синяя кнопка
			int prevStrNum = previousColoredCellNum / 10;
			int prevRowNum = previousColoredCellNum % 10;
			arrayOfButtons[prevStrNum][prevRowNum].updateButtClass();       // возвращаем ей нормальный цвет
			arrayOfButtons[prevStrNum][prevRowNum].updateButtonHtmlCode();
		}
		previousColoredCellNum = numOfCell;
	};

	private void updateButtType() {    // определяет тип кнопки
		if (isOpen == true) {
			buttType = "button";
		} else {
			buttType = "submit";
		}
	}

	private void getButtValue() {    // определяет отображаемое значение кнопки
		if (isOpen == true) {        // если кнопка открыта - отображает число
			buttValue = Sudoku.bigGameArray[butStrNum][butRowNum] + "";
		} else {                     // если закрыта - пробел
			buttValue = "&nbsp;";
		}
	}

	private static void SetButtValue(int strNum, int rowNum, int newValue) {   // назначает значение кнопки
		arrayOfButtons[strNum][rowNum].buttValue = newValue + "";
		arrayOfButtons[strNum][rowNum].updateButtonHtmlCode();
	}

	private void updateButtClass() {      // обновляет CSS стиль (цвет) кнопки 
		if (Sudoku.gameStatus == 1 & previousColoredCellNum != (butStrNum * 10 + butRowNum))
          	// если первое нажатие кнопки 
		{ 		    
			buttClass = "buttdrk";  // синий цвет
		} else if ((butStrNum > 2 & butStrNum < 6)    // если четные блоки
				^ (butRowNum > 2 & butRowNum < 6)) {
			buttClass = "bdrk";   // темно желтый цвет
		} else {
			buttClass = "butt";    // желтый цвет
		}
	}

	private void getButtOpenStatus() {
		isOpen = Sudoku.openButtonsArray[butStrNum][butRowNum];
	}

}