package com.augurworks.decisiontree.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.augurworks.decisiontree.ColumnResult;
import com.augurworks.decisiontree.Copyable;
import com.augurworks.decisiontree.Row;
import com.augurworks.decisiontree.RowGroup;
import com.augurworks.decisiontree.TypeOperatorLimit;

public class RowGroupImpl<K extends Copyable<K>, V extends Copyable<V>, T extends Copyable<T>> implements 
		RowGroup<K, V, T>, Copyable<RowGroup<K,V,T>> {
	private static final long serialVersionUID = 1L;
	List<Row<K, V, T>> rows = new ArrayList<Row<K, V, T>>();

	@Override
	public void addRow(Row<K, V, T> row) {
		rows.add(row);
	}

	@Override
	public int size() {
		return rows.size();
	}

	@Override
	public Row<K, V, T> getRow(int index) {
		return rows.get(index);
	}

	@Override
	public ColumnResult<K,V,T> getColumnResults(K input) {
		ColumnResult<K,V,T> column = new ColumnResultImpl<K,V,T>(input);
		for (Row<K,V,T> row : rows) {
			column.putResult(row.get(input), row.getResult());
		}
		return column;
	}

	@Override
	public Set<K> getColumnSet() {
		Set<K> columns = new HashSet<K>();
		if (rows.size() == 0) {
			return columns;
		} else {
			return rows.get(0).getColumnSet();
		}
	}

	@Override
	public List<T> getResults() {
		List<T> results = new ArrayList<T>();
		for (Row<K,V,T> row : rows) {
			results.add(row.getResult());
		}
		return results;
	}

	@Override
	public double getOriginalEntropy() {
		List<T> results = getResults();
		Map<T, Integer> counts = new HashMap<T, Integer>();
		for (T result : results) {
			if (counts.containsKey(result)) {
				counts.put(result, 1 + counts.get(result));
			} else {
				counts.put(result, 1);
			}
		}	
		Map<T, Double> probs = new HashMap<T, Double>();
		for (T result : counts.keySet()) {
			probs.put(result, counts.get(result) / (1.0 * results.size()));
		}
		double entropy = 0;
		for (T result : probs.keySet()) {
			entropy -= probs.get(result) * Math.log(probs.get(result)) / Math.log(2); 
		}
		return entropy;
	}

	@Override
	public double getEntropy(TypeOperatorLimit<K,V> tol) {
		ColumnResult<K,V,T> columnResults = getColumnResults(tol.getType());
		// count of results T when K has value V
		Map<T, Integer> countsPositive = new HashMap<T, Integer>();
		double posSize = 0;
		// count of results T when K does not have value V
		Map<T, Integer> countsNegative = new HashMap<T, Integer>();
		double negSize = 0;
		
		for (int i = 0; i < columnResults.size(); i++) {
			if (tol.getOperator().evaluate(columnResults.getValues().get(i), tol.getLimit())) {
				if (countsPositive.containsKey(columnResults.getResults().get(i))) {
					countsPositive.put(columnResults.getResults().get(i), countsPositive.get(columnResults.getResults().get(i)) + 1);
				} else {
					countsPositive.put(columnResults.getResults().get(i), 1);
				}
				posSize++;
			} else {
				if (countsNegative.containsKey(columnResults.getResults().get(i))) {
					countsNegative.put(columnResults.getResults().get(i), countsNegative.get(columnResults.getResults().get(i)) + 1);
				} else {
					countsNegative.put(columnResults.getResults().get(i), 1);
				}
				negSize++;
			}
		}
		Map<T, Double> probsPositive = new HashMap<T, Double>();
		Map<T, Double> probsNegative = new HashMap<T, Double>();
		double wholeSize = columnResults.size();
		
		for (T result : countsPositive.keySet()) {
			// number of rows where K == column
			probsPositive.put(result, countsPositive.get(result) / (posSize));
		}
		
		for (T result : countsNegative.keySet()) {
			// number of rows where K == column
			probsNegative.put(result, countsNegative.get(result) / (negSize));
		}
		double entropy = 0;
		
		double holder = 0;
		for (T result : probsPositive.keySet()) {
			holder -= probsPositive.get(result) * Math.log(probsPositive.get(result)) / Math.log(2);
		}
		holder *= posSize / wholeSize;
		
		entropy += holder;
		holder = 0;
				
		for (T result : probsNegative.keySet()) {
			holder -= probsNegative.get(result) * Math.log(probsNegative.get(result)) / Math.log(2);
		}
		holder *= negSize / wholeSize;
		entropy += holder;
		
		return entropy;
	}

	@Override
	public double getInformationGain(TypeOperatorLimit<K, V> tol) {
		return getOriginalEntropy() - getEntropy(tol);
	}

	@Override
	public RowGroup<K, V, T> withoutType(K type) {
		RowGroup<K, V, T> newRowGroup = new RowGroupImpl<K, V, T>();
		for (Row<K,V,T> row : rows) {
			Row<K,V,T> newRow = row.withoutKey(type);
			newRowGroup.addRow(newRow);
		}
		return newRowGroup;
	}

	@Override
	public RowGroup<K, V, T> satisfying(TypeOperatorLimit<K,V> tol) {
		RowGroupImpl<K,V,T> copy = new RowGroupImpl<K,V,T>();
		for (Row<K,V,T> oldRow : rows) {
			if (oldRow.matches(tol)) {
				copy.addRow(oldRow.copy());
			}
		}
		return copy;
	}
	
	@Override
	public RowGroup<K, V, T> notSatisfying(TypeOperatorLimit<K,V> tol) {
		RowGroupImpl<K,V,T> copy = new RowGroupImpl<K,V,T>();
		for (Row<K,V,T> oldRow : rows) {
			if (!oldRow.matches(tol)) {
				copy.addRow(oldRow.copy());
			}
		}
		return copy;
	}

	@Override
	public RowGroup<K, V, T> copy() {
		RowGroupImpl<K,V,T> copy = new RowGroupImpl<K,V,T>();
		for (Row<K,V,T> oldRow : rows) {
			copy.addRow(oldRow.copy());
		}
		return copy;
	}

	@Override
	public T getDomininantResult() {
		if (rows.size() == 0) {
			return null;
		}
		Map<T,Integer> map = new HashMap<T,Integer>();
		for (Row<K,V,T> oldRow : rows) {
			if (map.containsKey(oldRow.getResult())) {
				map.put(oldRow.getResult(), map.get(oldRow.getResult()) + 1);
			} else {
				map.put(oldRow.getResult(), 1);
			}
		}
		T dominant = null;
		int num = -1;
		for (T result : map.keySet()) {
			if (map.get(result).intValue() > num) {
				num = map.get(result).intValue();
				dominant = result;
			}
		}
		return dominant;
	}

	@Override
	public String toString() {
		return "RowGroupImpl [rows=" + rows + "]";
	}

}
