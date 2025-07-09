package syntacticanalysis;

public class Token {
    private TypeEnum type;
    private String lexeme;
    private Position position;

    public Token(TypeEnum typeEnum, String lexeme, Position position) {
        this.type = typeEnum;
        this.lexeme = lexeme;
        this.position = position;
    }

    public Token(TypeEnum typeEnum, Position position) {
        this.type = typeEnum;
        this.lexeme = typeEnum.getValor();
        this.position = position;
    }

    public TypeEnum getType() {
        return type;
    }

    public void setType(TypeEnum type) {
        this.type = type;
    }

    public String getLexeme() {
        return lexeme;
    }

    public void setLexeme(String lexeme) {
        this.lexeme = lexeme;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "Token{" + "type=" + type +", lexeme=\"" + lexeme + '\"' + ", position=" + position +"}";
    }
}

