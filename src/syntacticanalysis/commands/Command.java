package syntacticanalysis.commands;

public sealed interface Command permits AssignCommand, IfCommand, ReturnCommand, VarDeclCommand, WhileCommand {
    void printTree(String prefix);
}
