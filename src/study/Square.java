package study;

import javax.swing.*;
import java.awt.*;

public class Square extends JButton{
    public boolean canHear;
    private FigType type;
    private color col;
    private boolean firstM;
    private int lett;
    private int numb;
    private boolean canMove=true;

    public Square(int Nlett, int numb, FigType type, color col) {
        canHear = false;
        lett = Nlett;
        this.numb = numb;
        this.type = type;
        this.col = col;
        firstM = (type == FigType.pawn);
    }

    public color getColor() {
        return col;
    }

    public FigType getFig() {
        return type;
    }

    public boolean isFirstM() {
        return firstM;
    }

    public void setFig(Icon ic, FigType ft, color cl) {
        setIcon(ic);
        type = ft;
        col = cl;
    }

    public void setFig(FigType ft, color cl) {
        type = ft;
        col = cl;
    }

    public void smartSetIcon(String nameFile, int H, int W) {
        ImageIcon icon = new ImageIcon(nameFile);
        icon.setImage(icon.getImage().getScaledInstance(H, W, Image.SCALE_AREA_AVERAGING));
        setIcon(icon);
    }
    public void dontMove(){
        canMove=false;
    }
    public boolean canMove(int y, int x, color target) {
        if ((lett == x && numb == y) || col == null|| !canMove)
            return false;
        return (type == FigType.pawn && canMovePawn(y, x, target)) ||
                (type == FigType.castle && canMoveCactle(y, x)) ||
                (type == FigType.bishop && canMoveBishop(y, x)) ||
                (type == FigType.queen && canMoveQueen(y, x)) ||
                (type == FigType.king && canMoveKing(y, x)) ||
                (type == FigType.knight && canMoveKnight(y, x));
    }

    private boolean canMovePawn(int y, int x, color target) {
        if (target == null && Math.abs(lett - x) == 0) {
            if (Math.abs(numb - y) == 1)
                return true;
            else if (Math.abs(numb - y) == 2 && firstM == true)
                return true;
        } else if (target == Game.teamEn && Math.abs(lett - x) == 1 && Math.abs(numb - y) == 1)
            return true;
        return false;
    }

    private boolean canMoveCactle(int y, int x) {
        return Math.abs(lett - x) == 0 || Math.abs(numb - y) == 0;
    }

    private boolean canMoveBishop(int y, int x) {
        return Math.abs(lett - x) == Math.abs(numb - y);
    }

    private boolean canMoveQueen(int y, int x) {
        return canMoveBishop(y, x) || canMoveCactle(y, x);
    }

    private boolean canMoveKing(int y, int x) {
        return Math.abs(lett - x) <= 1 && Math.abs(numb - y) <= 1;
    }

    private boolean canMoveKnight(int y, int x) {
        return Math.abs(lett - x) == 2 && Math.abs(numb - y) == 1 || Math.abs(lett - x) == 1 && Math.abs(numb - y) == 2;
    }
}
