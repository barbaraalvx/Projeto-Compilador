package syntacticanalysis.commands;

sealed public interface Cmd permits IfCmd, WhileCmd, AssignCmd { }
