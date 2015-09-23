/* Решает задачу судоку 2 методами
 * 1 - для каждой закрытой кнопки собирает все отгаданные числа в строке, ряду и блоке и если их 8, то ответ - 9 число     bigExactChekCycle
 * 2 - для каждого не отгаданного числа в каждом блоке проверяет количество вариантов размещения этого числа (методом исключения),
 * если вариант только один - это и есть ответ       bigPossibleChekCycle
 * */


package sudoku_2015;

import java.util.ArrayList;
import java.util.HashSet;


class SudokuSolver {
	private int[][] gameSolveArray;                  // основной массив для решения
	private int numOfOpenedButtons = 0;              // кол-во открытых кнопок (разгаданных чисел)
	private boolean[][] openButtonsSolverArray = new boolean[9][9];   // булевый массив открытых кнопок
	private int arrayLength;
	private ArrayList<HashSet<Integer>> strHashArray = new ArrayList<HashSet<Integer>>(9);  
	// список из 9 коллекций, содержащих числа, входящих в каждую строку
	private ArrayList<HashSet<Integer>> rowHashArray = new ArrayList<HashSet<Integer>>(9);
	// список из 9 коллекций, содержащих числа, входящих в каждый ряд
	private ArrayList<HashSet<Integer>> blockHashArray = new ArrayList<HashSet<Integer>>(9);
	// список из 9 коллекций, содержащих числа, входящих в каждый блок ( 1 блок = 3*3 кнопки)
	private HashSet<Integer> cellHashSet = null;     // коллекция чисел, используемая для вычислений ответа каждой кнопки
	private int emptyCycleCounter;                   // счетчик пустых циклов вычислений
	private boolean hasOnlyOneSolution = false;      // конечный результат

	SudokuSolver(int[][] incomeArray) {
		gameSolveArray = incomeArray;
		arrayLength = incomeArray.length;
		createOpenButtonsArray();
		initAllHashArrays();
		while (emptyCycleCounter < 3) {
			bigPossibleChekCycle();
			bigExactChekCycle();
		}
		if (numOfOpenedButtons == 81) {
			hasOnlyOneSolution = true;
		}
	} 
	
	boolean sudokuChek(){		
		return hasOnlyOneSolution;
	}


	private void createOpenButtonsArray() {           // генерирует массив открытых кнопок
		for (int i = 0; i < arrayLength; i++) {
			for (int j = 0; j < arrayLength; j++) {
				if (gameSolveArray[i][j] > 0) {
					openButtonsSolverArray[i][j] = true;
					numOfOpenedButtons++;
				} else {
					openButtonsSolverArray[i][j] = false;
				}
			}
		}
	}

	private void initAllHashArrays() {              // инициирует все списки коллекций
		for (int i = 0; i < arrayLength; i++) {
			strHashArray.add(getAllStrNumbers(i));
			rowHashArray.add(getAllRowNumbers(i));
			blockHashArray.add(getAllBlockNumbers(i));
		}
	}

	private HashSet<Integer> getAllRowNumbers(int rowNum) {          //  возвращает коллекцию всех чисел в данном столбце
		HashSet<Integer> rowHashSet = new HashSet<Integer>();

		for (int i = 0; i < arrayLength; i++) {
			rowHashSet.add(gameSolveArray[i][rowNum]);
		}
		rowHashSet.remove(0);                                       // ноль не учитываем
		return rowHashSet;
	}

	private HashSet<Integer> getAllStrNumbers(int strNum) {         //  возвращает коллекцию всех чисел в данной строке
		HashSet<Integer> strHashSet = new HashSet<Integer>();

		for (int i = 0; i < arrayLength; i++) {
			strHashSet.add(gameSolveArray[strNum][i]);
		}
		strHashSet.remove(0);
		return strHashSet;
	}

	private HashSet<Integer> getAllBlockNumbers(int blockNum) {    //  возвращает коллекцию всех чисел в данном блоке
		HashSet<Integer> blockHashSet = new HashSet<Integer>();
		int coordX = (blockNum / 3) * 3;
		int coordY = (blockNum % 3) * 3;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				blockHashSet.add(gameSolveArray[i + coordX][j + coordY]);
			}
		}
		blockHashSet.remove(0);
		return blockHashSet;
	}

	private int calculateNumFromHash(HashSet<Integer> cellHashSet) {     
		// принимает коллекцию из 8 чисел и возвращает единственное отсутствующее число
		if (cellHashSet.size() != 8) {
			Sudoku.printHtml("<br>"	+ "Wrong cellHashSet for calculateNumFromHash" + "<br>");
			return 100;
		}
		for (int i = 1; i < (arrayLength + 1); i++) {
			if (!cellHashSet.contains(i)) {
				return i;
			}
		}
		Sudoku.printHtml("<br>" + "Wrong cellHashSet for calculateNumFromHash"+ "<br>");
		return 100;
	}

	private void changeCellValue(int strNum, int rowNum, int newValue) {    // при отгадывании числа  
		emptyCycleCounter = 0;                           // обнуляем счетчик пустых циклов
		openButtonsSolverArray[strNum][rowNum] = true;
		numOfOpenedButtons++;
		gameSolveArray[strNum][rowNum] = newValue;
		strHashArray.get(strNum).add(newValue);          // дополняем 3 соответствующие коллекции отгаданным числом 
		rowHashArray.get(rowNum).add(newValue);
		int blockNum = getBlockNum(strNum, rowNum);
		blockHashArray.get(blockNum).add(newValue);
	}

	private int getBlockNum(int strNum, int rowNum) {     // возвращает номер блока кнопки 
		int vertCoord = strNum / 3;
		int horizCoord = rowNum / 3;
		return (vertCoord * 3 + horizCoord);
	}

	private void bigPossibleChekCycle() {       // проверяет все блоки 
		emptyCycleCounter++;
		for (int i = 0; i < arrayLength; i++) {
			chekBlock(i);
		}
	}

	private boolean chekBlock(int blockNum) {   
		// для каждого неотгаданного числа в блоке проверяет количество вариантов размещения этого числа (методом исключения),
		// если вариант только один - это и есть ответ
		boolean isChange = false;

		int coordX = (blockNum / 3) * 3;
		int coordY = (blockNum % 3) * 3;
		for (int n = 1; n < (arrayLength + 1); n++) {
			if (blockHashArray.get(blockNum).contains(n) == false) {
				if (canBeNumInBlock(coordX, coordY, n) == true) {
					isChange = true;
				}
			}
		}
		return isChange;
	}

	private boolean canBeNumInBlock(int coordX, int coordY, int newNum) {  
		// проверяет, сколько раз число newNum может входить в этот блок
		boolean cellFlag = false;
		int strOfCell = 10;
		int rowOfCell = 10;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if ((openButtonsSolverArray[i + coordX][j + coordY] == false)
						&& canBeNumInCell(i + coordX, j + coordY, newNum) == true) {
					if (cellFlag == true) {
						return false;
					} else {
						cellFlag = true;
						strOfCell = i + coordX;
						rowOfCell = j + coordY;
					}
				}
			}
		}
		if (cellFlag == true) {                                // если может входить только 1 раз, меняем значение кнопки
			changeCellValue(strOfCell, rowOfCell, newNum);   
		}
		return cellFlag;
	}

	private boolean canBeNumInCell(int strNum, int rowNum, int newNum) {
		// проверяет, может ли newNum находится в этой ячейке (кнопке) 
		if (strHashArray.get(strNum).contains(newNum)) {   // если такое число уже входит в строку
			return false;
		} else if (rowHashArray.get(rowNum).contains(newNum)) {   // если такое число уже входит в ряд
			return false;
		} else {
			return true;    // иначе может входить 
		}
	}

	private void bigExactChekCycle() {
		// для каждой кнопки собирает все отгаданные числа в строке, ряду и блоке и если их 8, то ответ - 9 число
		emptyCycleCounter++;
		for (int i = 0; i < arrayLength; i++) {
			for (int j = 0; j < arrayLength; j++) {
				if (openButtonsSolverArray[i][j] == false) {
					cellHashSet = new HashSet<Integer>(strHashArray.get(i));        // берем копию коллекции чисел в строке
					cellHashSet.addAll(rowHashArray.get(j));                        // прибавляем воллекцию чисел ряда
					cellHashSet.addAll(blockHashArray.get(getBlockNum(i, j)));      // и коллекцию чисел блока
					if (cellHashSet.size() > 7) {                                   // Если в сумме получаем 8 чисел, 
						changeCellValue(i, j, calculateNumFromHash(cellHashSet));     // то 9 число и есть ответ
					}
				}
			}
		}
	}
}