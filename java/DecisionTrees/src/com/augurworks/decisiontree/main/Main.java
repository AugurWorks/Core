package com.augurworks.decisiontree.main;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.augurworks.decisiontree.BinaryNode;
import com.augurworks.decisiontree.BinaryOperator;
import com.augurworks.decisiontree.BinaryOperatorSet;
import com.augurworks.decisiontree.Provider;
import com.augurworks.decisiontree.RowGroup;
import com.augurworks.decisiontree.impl.BinaryNodeImpl;
import com.augurworks.decisiontree.impl.BinaryOperatorDoubleImpl;
import com.augurworks.decisiontree.impl.CopyableDouble;
import com.augurworks.decisiontree.impl.CopyableString;
import com.augurworks.decisiontree.impl.DecisionTrees;
import com.augurworks.decisiontree.impl.ModelType;
import com.augurworks.decisiontree.impl.TreeWithStats;

public class Main {
    public static RowGroup<CopyableString, CopyableDouble, ModelType> parseFile(String filepath) {
        return DecisionTrees.parseData(filepath, new Provider<CopyableString>() {
            @Override
            public CopyableString fromString(String s) {
                return CopyableString.from(s);
            }
        }, new Provider<CopyableDouble>() {
            @Override
            public CopyableDouble fromString(String s) {
                return CopyableDouble.valueOf(s);
            }
        }, new Provider<ModelType>() {
            @Override
            public ModelType fromString(String s) {
                return ModelType.fromString(s);
            }
        });
    }

    public static TreeWithStats<CopyableString, CopyableDouble, ModelType> runJob(RowGroup<CopyableString, CopyableDouble, ModelType> rows) {
        BinaryNode<CopyableString, CopyableDouble, ModelType> root = new BinaryNodeImpl<CopyableString, CopyableDouble, ModelType>(
                ModelType.TWO_WEEK_DURING_DAY, ModelType.FOUR_WEEK_DURING_DAY, null);
        root = DecisionTrees.train(root, rows, new BinaryOperatorSet<CopyableDouble>() {
                @Override
                public Collection<BinaryOperator<CopyableDouble>> operators() {
                    Set<BinaryOperator<CopyableDouble>> output = new HashSet<BinaryOperator<CopyableDouble>>();
                    for (BinaryOperator<CopyableDouble> op : BinaryOperatorDoubleImpl.values()) {
                        output.add(op);
                    }
                    return output;
                }
            }, 2);
        return TreeWithStats.of(root, rows);
    }

    public static TreeWithStats<CopyableString, CopyableDouble, ModelType> runJob(String filepath) {
        RowGroup<CopyableString, CopyableDouble, ModelType> rows = parseFile(filepath);
        return runJob(rows);
    }

    public static void main(String[] args) {
//        String filepath = "/home/saf/code/aw_core/model_performance.csv";
        String filepath = "/home/saf/code/aw_core/model_performance2.csv";
        TreeWithStats<CopyableString, CopyableDouble, ModelType> tree = runJob(filepath);
        System.out.println("Tree: " + tree.getTree());
        System.out.println("Correctness: " + tree.getCorrectPercent());
        System.out.println(tree.getTree().toFancyString());
        System.out.println("Count of rows: " + tree.getRows().getResults().size());
        System.out.println("Columns: " + tree.getRows().getColumnSet().size());
        DecisionTrees.print(tree.getTree(), tree.getRows());
        System.exit(0);
    }
}
