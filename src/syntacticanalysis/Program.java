package syntacticanalysis;

import java.util.List;

public class Program {
    private final List<Declaration> declarations;
    private final Expression finalExpression;

    public Program(List<Declaration> declarations, Expression finalExpression) {
        this.declarations = declarations;
        this.finalExpression = finalExpression;
    }

    public List<Declaration> getDeclarations() {
        return declarations;
    }

    public Expression getFinalExpression() {
        return finalExpression;
    }

    public void printTree() {
        System.out.println("Program");
        System.out.println("├── Declarations:");
        for (Declaration decl : declarations) {
            decl.printTree("│   ");
        }
        System.out.println("└── Final Expression:");
        System.out.println("    └── " + finalExpression);
    }
}
