/* Cristina Estupinan
 * 
 * Title:  Player.java
 * Author: Wayne Snyder (waysnyder@gmail.com)
 * Date: 11/29/13
 * Purpose: This is a simple random player provided as part of the code distribution for HW 08 for CS 112, Fall 2014
 */

import java.util.*; 

// Red is the human player and goes first
// class Player represents the Blue player, responding to the Red players moves.
public class Player {

	/* Original Player Class:

	private Random R = new Random(); 
	public int move(int[][] B) {
		int m = 0; 
		do {
			m = R.nextInt(8);
		} while(B[0][m] != 0);

		return m; 
	}*/

	private static int[][] B = new int[8][8];	//board

	private final static int C = 10; 			//computer   
	private final static int H = 1; 			//human
	private final static int BLANK = 0; 		//blank space, empty slot
	private final static int INF = 1000000; 
	private static final int D = 5;			//depth 


	/* Your player should be written so that it responds with the best move for ANY board you give it,
	 *		including the empty board or impossible boards (e.g., when there are five blue pieces and 2 red pieces).
	 *		This is a trivial complication, as your search should be able to start from any point! If the board is empty,
	 *		then you can provide a fixed initial move (e.g., in the middle of the board) or do a full search.
	 */


	// moves are 0 .. 64 (counting slots across rows and down columns)
	// empty slot is 0, Human (H) is 1 and Computer (C) is 10
	private static  int column(int move) {
		return move % 8;
	}

	private static  int row(int move) {
		return move / 8;
	}

	// -------------------------------------- MOVE FUNCTION ------------------------------------- // 
	public static  int move(int[][] B) { 
		int max = -1000;     
		int bestMove = -1;  

		for(int move = 0; move < 8; ++move) { 

			int row = findRow(B,column(move));

			if(B[row][column(move)] == BLANK) {   // move is available

				B[row][column(move)] = C;       // make the move

				int val = minMax(B, 1); 
				System.out.println("value = "+val);
				if(val > max) {                       // if better move, remember it
					bestMove = move; 
					max = val; 
				}

				B[row][column(move)] = BLANK;        // undo the move

			}  // end if
		}  // end for
		return bestMove;

	} 
	// ------------------------------------------------------------------------------------------- // 


	// ------------------------------------- MINMAX FUNCTION ------------------------------------- // 
	private static int minMax(int[][] B, int depth) { 
		return minMaxHelper(B, depth, -INF, INF);
	}

	private static int minMaxHelper(int[][]B, int depth, int alpha, int beta){

		if(isGameOver(B) || depth == D){ 
			int temp = evaluateBoard(B);
			return temp;  
		}


		else if(depth % 2 == 0) {       // even levels are max, C player           

			for(int move = 0; move < 8; ++move) {
				int row = findRow(B,column(move));


				if(B[row][column(move)] == 0) {   // move is available



					B[row][column(move)] = C;       // make the move  

					int val = minMaxHelper(B, depth + 1, alpha, beta);  

					if(val > alpha) { 
						alpha = val; 
					}

					B[row][column(move)] = BLANK;       // undo the move and try next move 
				}
			}
			return alpha; 
		} 


		else {                          // is a min node, H player
			for(int move = 0; move < 8; ++move) {
				int row = findRow(B,column(move));

				if(B[row][column(move)] == 0) {   // move is available

					B[row][column(move)] = H;       // make the move  

					int val = minMaxHelper(B, depth + 1, alpha, beta); 

					if(val < beta) { 
						beta = val; 
					}

					B[row][column(move)] = BLANK;       // undo the move and try next move    
				}
			}
			return beta; 
		}
	}


	// determines row where piece will fall based on column selected
	private static int findRow(int[][]B, int col){
		int row  = 0;
		for (int i = 0; i < 8; i++){
			if (B[i][col] == BLANK){
				row = i;
			}
		}
		return row;
	}
	// ------------------------------------------------------------------------------------------- // 


	// ---------------------------------- IS GAME OVER FUNCTION ---------------------------------- // 

	// return true if no blank slots
	private static  boolean isGameOver(int[][] B) {
		if(winForC(B) || winForH(B)){
			return true; 
		}
		else{
			for(int row = 0; row < 8; ++row) {
				for(int col = 0; col < 8; ++col) {

					if(B[row][col] == BLANK)
						return false;
				}
			}
			return true;
		}
	}
	// ------------------------------------------------------------------------------------------- // 




	// --------------------------------- EVALUATE BOARD FUNCTION --------------------------------- // 

	//this function returns high number if you're close to winning
	private static int evaluateBoard(int[][] B){
		int sum = 0;

		for(int row = 0; row < 8; row++){
			for (int  col = 0; col < 8; col++){
				sum += evaluateSpace(B, row, col);
			}
		}
		return sum;
	}
	// ------------------------------------------------------------------------------------------- // 


	// -------------------------------- EVALUATE SPACE FUNCTION ---------------------------------- // 
	private static int evaluateSpace(int[][] B, int row, int col){
		int sum = 0;

		//horizontal
		if (col <= 4){
			int[] InARow ={B[row][col], B[row][col+1], B[row][col+2], B[row][col+3]};
			sum += getValue(B, InARow);
		}

		//vertical
		if (row <= 4){
			int[] InARow = {B[row][col], B[row+1][col], B[row+2][col], B[row+3][col]};
			sum += getValue(B, InARow);
		}
		 
		//diagonal forward
		if (col <= 4 && row <= 4){
			int[] InARow = {B[row][col], B[row+1][col+1], B[row+2][col+2], B[row+3][col+3]};
			sum += getValue(B, InARow);
		}

		//diagonal backward
		if (col <= 4 && row >= 3){
			int[] InARow = {B[row][col], B[row-1][col+1], B[row-2][col+2], B[row-3][col+3]};
			sum += getValue(B, InARow);
		}
		return sum;
	}
	// ------------------------------------------------------------------------------------------- // 





	// ----------------------------------- GET VALUE FUNCTIONS ----------------------------------- // 

	// function returns the value of element in board space
	private static int getValue(int[][] B, int[] InARow){
		int sumC = 0;
		int sumH = 0;
		int numC = 0;
		int numH = 0;
		// horizontal, vertical, diagonal rows and cols are in array C
		// use this array find piece values


		boolean human = false;
		for (int i = 0; i < InARow.length; i++){
			if (InARow[i] == H){
				human = true;
				break;
			}
			if (InARow[i] == C){
				numC += 1;
			}
		}

		if (!human){
			sumC = (int) (Math.pow(numC, 2));
		}




		boolean computer = false;
		for (int i = 0; i < InARow.length; i++){
			if (InARow[i] == C){
				computer = true;
				break;
			}
			if (InARow[i] == H){
				numH += 1;
			}
		}

		if (!computer){
			sumH = -(int)(Math.pow(numH, 2));
		}



		int sum = sumC + sumH;


		return sum;
	}
	// ------------------------------------------------------------------------------------------- // 



	// ------------------------------------ WIN FOR FUNCTIONS ------------------------------------ // 
	private static boolean winForC(int[][] B) {
		return (checkLeftDiagonalWinForC(B) || checkRightDiagonalWinForC(B) || checkVerticalWinForC(B) || checkHorizonalWinForC(B));
	}


	private static boolean winForH(int[][] B) {
		return (checkLeftDiagonalWinForH(B) || checkRightDiagonalWinForH(B) || checkVerticalWinForH(B) || checkHorizonalWinForH(B));
	}



	private static boolean checkHorizonalWinForC(int[][] B){
		for (int row = 0; row < 8; row++){
			for (int col = 0; col < 5; col++){
				if (B[row][col]+B[row][col+1]+B[row][col+2]+B[row][col+3] == 40){
					return true;
				}
			}
		}
		return false;
	}

	private static boolean checkVerticalWinForC(int[][] B){
		for (int row = 0; row < 5; row++){
			for (int col = 0; col < 8; col++){
				if (B[row][col]+B[row+1][col]+B[row+2][col]+B[row+3][col] == 40){
					return true;
				}
			}
		}
		return false;
	}

	private static boolean checkRightDiagonalWinForC(int[][] B){
		for (int row = 0; row < 5; row++){
			for (int col = 0; col < 5; col++){
				if(B[row][col]+B[row+1][col+1]+B[row+2][col+2]+B[row+3][col+3] == 40){
					return true;
				}
			}
		}
		return false;
	}

	private static boolean checkLeftDiagonalWinForC(int[][] B){
		for (int row = 3; row < 8; row++){
			for (int col = 0; col < 5; col++){
				if(B[row][col]+B[row-1][col+1]+B[row-2][col+2]+B[row-3][col+3] == 40){
					return true;
				}
			}
		}
		return false;
	}

	private static boolean checkHorizonalWinForH(int[][] B){
		for (int row = 0; row < 8; row++){
			for (int col = 0; col < 5; col++){
				if (B[row][col]+B[row][col+1]+B[row][col+2]+B[row][col+3] == 4){
					return true;
				}
			}
		}
		return false;
	}

	private static boolean checkVerticalWinForH(int[][] B){
		for (int row = 0; row < 5; row++){
			for (int col = 0; col < 8; col++){
				if (B[row][col]+B[row+1][col]+B[row+2][col]+B[row+3][col] == 4){
					return true;
				}
			}
		}
		return false;
	}

	private static boolean checkRightDiagonalWinForH(int[][] B){
		for (int row = 0; row < 5; row++){
			for (int col = 0; col < 5; col++){
				if(B[row][col]+B[row+1][col+1]+B[row+2][col+2]+B[row+3][col+3] == 4){
					return true;
				}
			}
		}
		return false;
	}

	private static boolean checkLeftDiagonalWinForH(int[][] B){
		for (int row = 3; row < 8; row++){
			for (int col = 0; col < 5; col++){
				if(B[row][col]+B[row-1][col+1]+B[row-2][col+2]+B[row-3][col+3] == 4){
					return true;
				}
			}
		}
		return false;
	}
	// ------------------------------------------------------------------------------------------- // 




}

