/*        Генерирует задачу судоку:
 1 Создает базовый массив (в начале всегда одинаковый)
 2 Производит перемешивание массива случайным образом в соответствии с правилами
 3 Генерирует булевый массив, отвечающий за отображение необходимого количества открытых кнопок
 4 Совмещает данные массивы (в задачу судоку) и отправляет для решения в SudokuSolver
 5 Если решение дает отрицательный ответ (больше одного решения) - переходит к пункту 3
 6 При положительном ответе возвращает оба массива по отдельности     
*/
package sudoku_2015;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class SudokuGenerator {
	private int[][] baseArray = new int[9][9];             // базовый массив, 81 число
	private int[][] baseArrayWithZeros = new int[9][9];    // базовый массив с нулями вместо закрытых кнопок - для решения в SudokuSolver 
	private boolean [][] openButtonsArray = new boolean[9][9];    // массив открытых кнопок
	private int arrLength = baseArray.length;
	private int amountOfOpenButtons;                        // кол-во открытых кнопок
	private final int MIXING_NUM = 30;                      // кол-во итераций перемешивания baseArray
	private int attemptsCounter = 0;                        // кол-во попыток решений
	List<Long> timings = new ArrayList<Long>();             // список замеров времени вычисления

	int[][] generateSudokuArray() { 
		createBaseArray();
		mixSwapFunctions();                              
		createOpenButtonsArray();
		if (Sudoku.testMode == true){
			printTimeStats(timings);
			Sudoku.printIntArray(baseArray);		
		}		
		return baseArray;
	}

	private int[][] createBaseArray() {                        // создает базовый массив (в начале всегда одинаковый)
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				baseArray[i][j] = (i * 3 + i / 3 + j) % 9 + 1;
			}
		}
		return baseArray;
	}
	
	boolean [][] getOpenButtonsArray(){
		return openButtonsArray;
	}

	private void createBaseArrayWithZeros(int[][] inArray) {      // готовит массив к решению, заполняя закрытые кнопки нулями
		for (int i = 0; i < arrLength; i++) {
			for (int j = 0; j < arrLength; j++) {
				if (openButtonsArray[i][j] == false) {
					baseArrayWithZeros[i][j] = 0;
				} else {
					baseArrayWithZeros[i][j] = baseArray[i][j];
				}
			}
		}
	}

	

	private void transposeArr() {                             // транспонирует массив, строки становятся столбцами, столбцы строками
		int[][] newArr = new int[arrLength][arrLength];
		for (int i = 0; i < arrLength; ++i) {
			for (int j = 0; j < arrLength; ++j) {
				newArr[j][i] = baseArray[i][j];
			}
		}
		baseArray = newArr;
	}

	private void swapSmallRows() {                             // обмен 2 столбцов                      
		int[] randNumArray = getTwoDiffRandomNum();
		int randomBigStr = (int) (Math.random() * 3);
		randNumArray[0] += randomBigStr * 3;
		randNumArray[1] += randomBigStr * 3;
		for (int j = 0; j < arrLength; j++) {
			swapCells(j, randNumArray[0], j, randNumArray[1]);
		}
	}

	private void swapSmallStr() {                               // обмен 2 строк 
		int[] randNumArray = getTwoDiffRandomNum();
		int randomBigStr = (int) (Math.random() * 3);
		randNumArray[0] += randomBigStr * 3;
		randNumArray[1] += randomBigStr * 3;
		for (int i = 0; i < arrLength; i++) {
			swapCells(randNumArray[0], i, randNumArray[1], i);
		}
	}

	private void swapBigRows() { //                              // обмен 2 вертикальных блоков  (1 вертимкальный блок - 3 столбца)
		int[] randNumArray = getTwoDiffRandomNum();
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < arrLength; j++) {
				swapCells(j, (randNumArray[0] * 3 + i), j,
						(randNumArray[1] * 3 + i));
			}
		}
	}

	private void swapBigStr() {                                 // обмен 2 горизонтальных блоков  
		int[] randNumArray = getTwoDiffRandomNum();
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < arrLength; j++) {
				swapCells((randNumArray[0] * 3 + i), j,
						(randNumArray[1] * 3 + i), j);
			}
		}
	}

	private void swapCells(int numStrOne, int numRowOne, int numStrTwo,      // меняет 2 ячейки (кнопки) местами
			int numRowTwo) {
		int tempValue = baseArray[numStrOne][numRowOne];
		baseArray[numStrOne][numRowOne] = baseArray[numStrTwo][numRowTwo];
		baseArray[numStrTwo][numRowTwo] = tempValue;
	}

	private int[] getTwoDiffRandomNum() {                              // генерирует 2 разных случайных числа [0:2]
		int randomFirstNumber = (int) (Math.random() * 3);
		int randomSecondNumber;
		do {
			randomSecondNumber = (int) (Math.random() * 3);
		} while (randomFirstNumber == randomSecondNumber);
		int outputArr[] = { randomFirstNumber, randomSecondNumber };
		return outputArr;
	}

	private void mixSwapFunctions() {                           // случайным образом перемешивает baseArray
		for (int i = 0; i < MIXING_NUM; i++) {
			int randomFuncNum = (int) (Math.random() * 5);
			switch (randomFuncNum) {
			case 0:
				transposeArr();
				break;
			case 1:
				swapSmallRows();
				break;
			case 2:
				swapSmallStr();
				break;
			case 3:
				swapBigRows();
				break;
			case 4:
				swapBigStr();
				break;
			}
		}
	}

	private void createOpenButtonsArray() {                        // генерирует массив открытых кнопок, обеспечивающий единственность решения
		amountOfOpenButtons = 35 - Sudoku.gameComplexity * 4;
		for (boolean[] matr : openButtonsArray) {
			Arrays.fill(matr, false);                               // изначально массив заполнен false
		}

		do {
			generateOpenButtonsArray();                              
		} while (chekOpenButtonsArray() == false);                    // до тех пор, пока нет единственного решения
		Sudoku.printHtml("<br>Sudoku generated: " + attemptsCounter);         // выводим на страницу количество попыток генерирования
		
	}

	private void generateOpenButtonsArray() {                       // случайным образом генерирует массив открытых кнопок
		int buttonsCounter = 0;
		while (buttonsCounter <= amountOfOpenButtons) {
			
			int i = (int) (Math.random() * arrLength);
			int j = (int) (Math.random() * arrLength);
			if (openButtonsArray[i][j] == false) {
				openButtonsArray[i][j] = true;
				buttonsCounter++;

			}
		}
	}

	private boolean chekOpenButtonsArray() {                  // проверяет корректность массива открытых кнопок в паре с рабочим массивом baseArray           
		attemptsCounter++;                                    // увеличиваем счетчик попыток
		createBaseArrayWithZeros(baseArray);  
		long startTime = System.nanoTime();
		SudokuSolver sSolver = new SudokuSolver(baseArrayWithZeros);
		long solveTime = System.nanoTime() - startTime;
		timings.add(solveTime);
		boolean hasOnlyOneSolution = sSolver.sudokuChek();    
		if(hasOnlyOneSolution ==false){                       // если нет единственного решения
			for (boolean[] matr : openButtonsArray) {
				Arrays.fill(matr, false);                     // обнуляем массив открытых кнопок
				}
			} 
		return hasOnlyOneSolution;	
		}
	
	private void printTimeStats(List<Long> timings){              // вычисляет время решения задач и выводит в виде HTML 
        long min = Collections.min(timings);
        long max = Collections.max(timings);  
        long sum = 0;
        
        for(long val : timings){
            sum += val; 
        }
        
        double avg = sum / timings.size();
        Sudoku.printHtml("<br><br>Generation timing: ");
        Sudoku.printHtml("<br>min: " + String.format( "%.4f", min*1e-6)+ " ms ");
        Sudoku.printHtml("<br>max:" + String.format( "%.4f", max*1e-6)+ " ms ");
        Sudoku.printHtml("<br>avg: " + String.format( "%.4f", avg*1e-6)+ " ms<br>");
    }
}