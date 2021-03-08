package edu.uky.ai.chess.ex;

import java.util.ArrayList;
import java.util.Iterator;

import edu.uky.ai.SearchBudget;
import edu.uky.ai.chess.Agent;
import edu.uky.ai.chess.state.Player;
import edu.uky.ai.chess.state.Queen;
import edu.uky.ai.chess.state.Rook;
import edu.uky.ai.chess.state.Bishop;
import edu.uky.ai.chess.state.Board;
import edu.uky.ai.chess.state.Knight;
import edu.uky.ai.chess.state.Pawn;
import edu.uky.ai.chess.state.Piece;
import edu.uky.ai.chess.state.State;

/**
 * This agent chooses a next move randomly from among the possible legal moves.
 * @JackHancock
 */
public class JCHA265 extends Agent {

	public JCHA265() {
		super("JCHA265");
	}

	@Override
	protected State chooseMove(State current) {
		// This list will hold all the children state (all possible next moves).
		ArrayList<State> children = new ArrayList<>();
		// Iterate through each child and put it in the list (as long as the
		// search budget hasn't been used up yet).
		Iterator<State> iterator = current.next().iterator();
		while(!current.budget.hasBeenExhausted() && iterator.hasNext())
			children.add(iterator.next());
		// Pick a next move at random.
		Result choice;
		if (current.player.equals(Player.BLACK)) {
			choice = findMin(current, 3, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
		}else {
			choice = findMax(current, 3, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
		}

		return choice.state;
	}
	
	private static int utility(State state) {
		if(state.movesUntilDraw == 0 || state.check == false) {
			return 0;
		}
		if (state.player == Player.WHITE) {
			return -1000;
		}else {
			return 1000;
		}
	}

	private static int material(Board board, Player player) {
		int material = 0;
		for(Piece piece : board)
			if(piece.player == Player.WHITE) {
				material += value(piece);
			}else {
				material -= value(piece);
			}
		return material;
	}
	
	private static int value(Piece piece) {
		if(piece instanceof Pawn)
			return 1;
		else if(piece instanceof Knight)
			return 3;
		else if(piece instanceof Bishop)
			return 3;
		else if(piece instanceof Rook)
			return 5;
		else if(piece instanceof Queen)
			return 9;
		// The piece must be a King.
		else
			return 100;
	}

	public class Result {

		public State state;
		public double value;
		public double a;
		public double b;

		public Result(State state, double value, double a, double b) {
			this.state = state;
			this.value = value;
			this.a = a;
			this.b = b;
		}
	}
	
	private Result IDDFS(State current, int limit) {
		Result best = new Result(current, 0, 0, 0);
		for (int depth = 1; depth <= limit; depth++) {
			SearchBudget sb = new SearchBudget(500000, 300000);
			Result temp;
			if (current.player.equals(Player.BLACK)) {
				temp = findMin(current, depth, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
			}else {
				temp = findMax(current, depth, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
			}
		}
		return best;
	}
	
	
	private Result findMax(State current, int distance, double a, double b) {
		if(current.over){
			return new Result(current, utility(current), a, b);
		}
		if (distance == 0) {
			return new Result(current, material(current.board, current.player), a, b);
		}
		distance -= 1;
		Result best = new Result(current, Double.NEGATIVE_INFINITY, a, b);
		for (State i: current.next()) {
			double child_value = findMin(i, distance, a, b).value;
			if (child_value > best.value) {
				best = new Result(i, child_value, a, b);
			}
			if (best.value >= b) {
				return best;
			}
			a = Math.max(a, best.value);
		}
		return best;
	}
	
	private Result findMin(State current, int distance, double a, double b) {
		if(current.over){
			return new Result(current, utility(current), a, b);
		}
		if (distance == 0) {
			return new Result(current, material(current.board, current.player), a, b);
		}
		distance -= 1;
		Result best = new Result(current, Double.POSITIVE_INFINITY, a, b);
		for (State i: current.next()) {
			double child_value = findMax(i, distance, a, b).value;
			if (child_value < best.value) {
				best = new Result(i, child_value, a, b);
			}
			if (best.value <= a) {
				return best;
			}
			b = Math.min(b, best.value);
		}
		return best;
	}

	
}
