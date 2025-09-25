package syntacticanalysis.commands;

sealed public interface Command permits IfCommand, WhileCommand, AssignCommand { }
