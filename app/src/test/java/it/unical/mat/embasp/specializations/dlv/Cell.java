package it.unical.mat.embasp.specializations.dlv;

import it.unical.mat.embasp.languages.Predicate;
import it.unical.mat.embasp.languages.Term;

@Predicate("cell")
public class Cell {
	@Term(0)
	private int row;
	@Term(1)
	private int column;
	@Term(2)
	private int value;

	public Cell() {
		row = column = value = 0;
	}

	public Cell(final int row, final int column, final int value) {
		this.row = row;
		this.column = column;
		this.value = value;
	}

	public int getColumn() {
		return column;
	}

	public int getRow() {
		return row;
	}

	public int getValue() {
		return value;
	}

	public void setColumn(final int column) {
		this.column = column;
	}

	public void setRow(final int row) {
		this.row = row;
	}

	public void setValue(final int value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Cell{" + "row=" + row + ", column=" + column + ", value=" + value + '}';
	}
}
