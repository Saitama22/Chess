package study;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

public class Game extends JFrame {
    public static color teamMe;
    public static color teamEn;
    private int buttonHeight, buttonWidth;
    private int actLett;
    private int actNumb;
    boolean ShahW, ShahB;
    private int WKingX, WKingY;
    private int BKingX, BKingY;
    private Square[][] butt;
    JButton[] tempButt;
    Container container;
    boolean PausedPawn;
    boolean checking;

    public Game() {
        super("GridLayoutTest");
        checking = false;
        teamMe = color.white;
        teamEn = color.black;
        WKingY = 7;
        WKingX = 4;
        BKingY = 0;
        BKingX = 4;
        buttonHeight = 50;
        buttonWidth = 50;
        actNumb = -1;
        ShahB = false;
        ShahW = false;
        PausedPawn = false;
        butt = new Square[8][8];
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel grid = new JPanel(new GridLayout(8, 8, 0, 0));
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                butt[i][j] = new Square(j, i, detType(i, j), detColor(i));
                butt[i][j].smartSetIcon(detIcon(i, j), buttonHeight, buttonWidth);
                butt[i][j].setPreferredSize(new Dimension(buttonWidth, buttonHeight));
                if ((i + j) % 2 == 0) butt[i][j].setBackground(Color.white);
                else butt[i][j].setBackground(Color.black);
                butt[i][j].addActionListener(this::act);
                grid.add(butt[i][j]);
            }
        }
        container = getContentPane();
        container.add(grid);
        grid = new JPanel(new GridLayout(0, 9, 0, 0));
        for (int i = 0; i < 9; i++) {
            String simb = "";
            if (i != 0)
                simb = "      " + Character.toString((char) 64 + i);
            JLabel lab = new JLabel(simb);
            lab.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
            grid.add(lab);
        }
        container.add(grid, BorderLayout.SOUTH);
        grid = new JPanel(new GridLayout(8, 0, 0, 0));
        for (int i = 0; i < 8; i++) {
            JLabel lab = new JLabel("      " + Character.toString((char) 49 + i));
            lab.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
            grid.add(lab);
        }
        container.add(grid, BorderLayout.WEST);
            pack();
            setVisible(true);
    }

    public String detIcon(int y, int x) {
        String name;
        if (butt[y][x].getFig() == null) return null;
        name = butt[y][x].getColor().toString();
        name = name.concat(butt[y][x].getFig().toString());
        String str = "data/";
        str = str.concat(name);
        str = str.concat(".png");
        return str;

    }
    public FigType detType(int y, int x) {
        if (y > 1 && y < 6) return null;
        if (y == 1 | y == 6)
            return FigType.pawn;
        else if (x == 0 || x == 7)
            return FigType.castle;
        else if (x == 1 || x == 6)
            return FigType.knight;
        else if (x == 2 || x == 5)
            return FigType.bishop;
        else if (x == 3)
            return FigType.queen;
        else if (x == 4)
            return FigType.king;
        else return null;
    }
    public color detColor(int y) {
        if (y > 1 && y < 6) return null;
        else if (y <= 1)
            return color.black;
        else
            return color.white;
    }
    private String EatFig(int q, int w) {
        return detIcon(q, w).replaceFirst(".png", "Eat.png");
    }
    private void paint() {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                if (butt[i][j].canHear) {
                    if (butt[i][j].getColor() == null)
                        butt[i][j].smartSetIcon("data/canMove.png", buttonHeight, buttonWidth);
                    else if (butt[i][j].getColor() == teamEn)
                        butt[i][j].smartSetIcon(EatFig(i, j), buttonWidth, buttonHeight);
                }

    }

    private boolean detMoves(int i, int j, int q, int w) {
        if (q > 7 || q < 0 || w > 7 || w < 0)
            return false;
        Square temp = new Square(q, w, butt[q][w].getFig(), butt[q][w].getColor());
        temp.setIcon(butt[q][w].getIcon());

        butt[q][w].setFig(butt[i][j].getFig(), butt[i][j].getColor());
        butt[i][j].setFig(null, null);
        changeKing(q,w);
        if (checkShah() == teamMe) {
            butt[i][j].setFig(butt[q][w].getFig(), butt[q][w].getColor());
            butt[q][w].setFig(temp.getFig(), temp.getColor());
            changeKing(i,j);
            return false;
        }
        butt[i][j].setFig(butt[q][w].getFig(), butt[q][w].getColor());
        butt[q][w].setFig(temp.getFig(), temp.getColor());
        changeKing(i,j);

        if (butt[i][j].canMove(q, w, butt[q][w].getColor())) {
            if (butt[q][w].getColor() == null) {
                butt[q][w].canHear = true;
                return true;
            } else if (butt[q][w].getColor() == teamEn) {
                butt[q][w].canHear = true;
                return false;
            }
        }
        return false;
    }
    private void check(int i, int j, int aux) {
        boolean dir1 = true, dir2 = true, dir3 = true, dir4 = true;
        for (int q = 1; q < 8; q++) {
            if (dir1) dir1 = detMoves(i, j, i + q, j + q * aux);
            if (dir2) dir2 = detMoves(i, j, i - q, j - q * aux);
            if (dir3) dir3 = detMoves(i, j, i + q * aux, j - q);
            if (dir4) dir4 = detMoves(i, j, i - q * aux, j + q);
            if (butt[i][j].getFig() == FigType.king)
                break;
        }
    }
    void checkAct(int i, int j) {
        if (butt[i][j].getFig() == FigType.pawn) {
            int UpDown;
            if (teamMe == color.white)
                UpDown = 1;
            else
                UpDown = -1;
            if (detMoves(i, j, i - UpDown, j))
                detMoves(i, j, i - UpDown * 2, j);
            detMoves(i, j, i - UpDown, j - 1);
            detMoves(i, j, i - UpDown, j + 1);
        } else if (butt[i][j].getFig() == FigType.castle)
            check(i, j, 0);
        else if (butt[i][j].getFig() == FigType.bishop)
            check(i, j, 1);
        else if (butt[i][j].getFig() == FigType.queen || butt[i][j].getFig() == FigType.king) {
            check(i, j, 0);
            check(i, j, 1);
        } else if (butt[i][j].getFig() == FigType.knight) {
            detMoves(i, j, i + 2, j + 1);
            detMoves(i, j, i + 2, j - 1);
            detMoves(i, j, i - 2, j + 1);
            detMoves(i, j, i - 2, j - 1);
            detMoves(i, j, i + 1, j + 2);
            detMoves(i, j, i + 1, j - 2);
            detMoves(i, j, i - 1, j + 2);
            detMoves(i, j, i - 1, j - 2);
        }
    }
    private void clear() {
        for (int q = 0; q < 8; q++)
            for (int w = 0; w < 8; w++) {
                if (butt[q][w].canHear) {
                    butt[q][w].canHear = false;
                    if (butt[q][w].getFig() != FigType.king)
                        butt[q][w].smartSetIcon(detIcon(q, w), buttonHeight, buttonWidth);
                }
            }

    }
    private void changeTeam() {
        if (true) {
            color teamtemp = teamMe;
            teamMe = teamEn;
            teamEn = teamtemp;
        }
    }

    private FigType detFriShah(int i, int j, color col) {
        if (i > 7 || i < 0 || j > 7 || j < 0|| butt[i][j].getFig()==null)
            return FigType.none;
        else if (butt[i][j].getColor() != col)
            return null;
        return butt[i][j].getFig();
    }
    private boolean menaceKing(color KingColor, int i, int j) {
        color enColor=color.white;
        if (KingColor == color.white)
            enColor = color.black;
            if (detFriShah(i + 1, j + 2, enColor)== FigType.knight ||
                detFriShah(i - 1, j + 2, enColor)== FigType.knight ||
                detFriShah(i + 1, j - 2, enColor)== FigType.knight ||
                detFriShah(i - 1, j - 2, enColor)== FigType.knight ||
                detFriShah(i + 2, j + 1, enColor)== FigType.knight ||
                detFriShah(i - 2, j + 1, enColor)== FigType.knight ||
                detFriShah(i + 2, j - 1, enColor)== FigType.knight ||
                detFriShah(i - 2, j - 1, enColor)== FigType.knight)
            return true;
        if (KingColor == color.white && (detFriShah(i - 1, j - 1, enColor)==FigType.pawn
                                     ||  detFriShah(i - 1, j + 1, enColor)==FigType.pawn))
            return true;
        else if (KingColor == color.black && (detFriShah(i + 1, j - 1, enColor) ==FigType.pawn
                                          ||  detFriShah(i + 1, j + 1, enColor)==FigType.pawn))
            return true;
        FigType dir;
        for (int k = 0; k < 2; k++) {
            boolean f=true,s=true,t=true,f4=true;
            FigType fig = FigType.castle;
            if(k==1)
                fig = FigType.bishop;
            for (int q = 1; q < 8; q++) {
                if (f) {
                    dir = detFriShah(i + q, j + q * k, enColor);
                    if (dir == FigType.queen || dir == fig || (q == 1 && dir == FigType.king))
                        return true;
                    else if (dir != FigType.none)
                        f = false;
                }
                if (s) {
                    dir = detFriShah(i - q, j - q * k, enColor);
                    if (dir == FigType.queen || dir == fig || (q == 1 && dir == FigType.king))
                        return true;
                    else if (dir != FigType.none)
                        s = false;
                }
                if (t) {
                    dir = detFriShah(i + q * k, j - q, enColor);
                    if (dir == FigType.queen || dir == fig || (q == 1 && dir == FigType.king))
                        return true;
                    else if (dir != FigType.none)
                        t = false;
                }
                if (f4) {
                    dir = detFriShah(i - q * k, j + q, enColor);
                    if (dir == FigType.queen || dir == fig || (q == 1 && dir == FigType.king))
                        return true;
                    else if (dir != FigType.none)
                        f4 = false;
                }
            }
        }
        return false;
    }
    private color checkShah() {
        if (menaceKing(color.white, WKingY, WKingX)) {
            return color.white;
        }
        if (menaceKing(color.black, BKingY, BKingX)) {
            return color.black;
        }
        return null;
    }
    private void changeKing(int i, int j) {
        if (butt[i][j].getFig() == FigType.king) {
            if (teamMe == color.white) {
                WKingY = i;
                WKingX = j;
            } else {
                BKingY = i;
                BKingX = j;
            }
        }
    }
    private void paintShah(){
        if (checkShah() == color.black)
            butt[BKingY][BKingX].smartSetIcon("data/blackkingSh.png", buttonHeight, buttonWidth);
        else if (checkShah() == color.white)
            butt[WKingY][WKingX].smartSetIcon("data/whitekingSh.png", buttonHeight, buttonWidth);
        else if (checkShah() == null) {
            butt[BKingY][BKingX].smartSetIcon("data/blackking.png", buttonHeight, buttonWidth);
            butt[WKingY][WKingX].smartSetIcon("data/whiteking.png", buttonHeight, buttonWidth);
        }
    }

    private void transformPawn(int y, int x, color ColPawn) {
        JPanel grid = new JPanel();
        tempButt = new JButton[4];
        for (int i = 0; i < 4; i++) {
            tempButt[i] = new JButton();
            tempButt[i].setBackground(Color.white);
            tempButt[i].setPreferredSize(new Dimension(buttonWidth, buttonHeight));
            String NameIc = "data/";
            NameIc = NameIc.concat(ColPawn.toString());
            if (i == 0)
                NameIc = NameIc.concat("queen");
            else if (i == 1)
                NameIc = NameIc.concat("bishop");
            else if (i == 2)
                NameIc = NameIc.concat("knight");
            else NameIc = NameIc.concat("castle");
            NameIc = NameIc.concat(".png");
            ImageIcon icon = new ImageIcon(NameIc);
            icon.setImage(icon.getImage().getScaledInstance(buttonWidth, buttonHeight, Image.SCALE_AREA_AVERAGING));
            tempButt[i].setIcon(icon);
            tempButt[i].addActionListener(this::TransForm);
        }
        JPanel grid2 = new JPanel();
        GridLayout layout2 = new GridLayout(0, 11, 0, 0);
        grid.setLayout(layout2);
        for (int i = 0; i < 6; i++) {
            if ((x < 4 && i - 1 == x) || (x >= 4 && i == 5))
                for (int j = 0; j < 4; j++)
                    grid2.add(tempButt[j]);
            else {
                Label l = new Label();
                l.setPreferredSize(new Dimension(50, 0));
                grid2.add(l);
            }
        }
        grid2.add(grid);
        container.add(grid2, "North");
        PausedPawn = true;
        pack();

    }
    private void TransForm(ActionEvent event) {
        container.remove(3);
        for (int j = 0; j < 4; j++) {
            FigType Ft;
            if (j == 0) Ft = FigType.queen;
            else if (j == 1) Ft = FigType.bishop;
            else if (j == 2) Ft = FigType.knight;
            else Ft = FigType.castle;
            if (tempButt[j].equals(event.getSource()))
                butt[actLett][actNumb].setFig(tempButt[j].getIcon(), Ft, teamMe);
        }
        changeTeam();
        actNumb = -1;
        PausedPawn = false;
        pack();
        paintShah();
    }

    private boolean isLose()  {
        for (int q = 0; q < 8; q++) {
            for (int w = 0; w < 8; w++) {
                if (butt[q][w].getColor()!=teamMe)
                    continue;
                else
                    checkAct(q,w);
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        if (butt[i][j].canHear) {
                            clear();
                            return false;
                        }
                    }
                }
            }
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                  butt[i][j].dontMove();
             //   butt[i][j].removeActionListener(this::act);
            }
        }
        String str;
        if (checkShah()!=null)
            str="Шах и мат.";
        else
            str="Пат. Ходов нет.";
        JOptionPane.showMessageDialog(this,str);
        return true;
    }
    private void act(ActionEvent event) {

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Square tempch = butt[i][j];
                if (tempch.equals(event.getSource())) {
                    if (butt[i][j].getColor() == teamMe) {
                        clear();
                        actLett = i;
                        actNumb = j;
                        isLose();
                        checkAct(i, j);
                        paint();
                    }
                        else if (actNumb != -1) {
                        if (butt[i][j].canHear && !PausedPawn) {
                        //     if (true){
                            butt[i][j].setFig(butt[actLett][actNumb].getIcon(), butt[actLett][actNumb].getFig(), butt[actLett][actNumb].getColor());
                            butt[actLett][actNumb].setFig(null, null, null);
                            changeKing(i, j);
                            paintShah();
                            actNumb = j;
                            actLett = i;
                            if ((i == 0 || i == 7) && butt[i][j].getFig() == FigType.pawn) {
                                transformPawn(i, j, teamMe);
                            }
                            else {
                                changeTeam();
                                actNumb = -1;
                            }
                        }
                        clear();
                    }
                    break;
                }
            }
        }
    }

    public static void main(String[] args) {
        new Game();
    }
}

