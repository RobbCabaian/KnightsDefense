import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
//import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;
 
import javax.imageio.ImageIO;
import javax.swing.*;
 
public class KnightsDefense 
{
    private Board GameBoard;
    ArrayList<ChessPiece> pieces;
    Knight TheKnight;
    Difficulty gameDiff;
    boolean endgame;
    double scoreMultiplier;
    double playerScore; 
     
    private JFrame Frame;
    private JPanel MainPanel;
    private JPanel MidPanel;
    private TileButton[][] Tiles;
     
    private Coordinates destination;
     
    public enum Difficulty
    {
        EASY, MEDIUM, HARD;
    }
    public KnightsDefense()
    {
        resetGame();
    }
    public void resetGame()
    {
        GameBoard = new Board();
        pieces = new ArrayList<ChessPiece>();
        endgame = false;
        playerScore=0;
        spawnKnight();
        pieces.clear();
        pieces.add(TheKnight);
    }
    //Internal class for the paintable and interactable JButtons.
    class TileButton extends JButton
    {
        private Coordinates CO;
        private Image tileImage;
        public TileButton(int row, int col)
        {
            CO = new Coordinates(row,col);
        }
        public Coordinates outputCoordinates()
        {
            return this.CO;
        }
        public void setImage(Image i)
        {
            tileImage = i;
            this.repaint();
        }
        public void clear()
        {
            tileImage = null;
            this.repaint();
        }
        @Override
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            g.drawImage(tileImage, 0, 0, null);
        }
    }
    //Internal class for paintable JPanels.
    class ImagePanel extends JPanel
    {
        private Image backGround;
        public ImagePanel()
        {
            super();
            try{
                backGround = ImageIO.read(new File(ChessPiece.filePathHeader+"\\Images\\Menu.png"));
            }catch(IOException e){
            }
        }
        public ImagePanel(LayoutManager layout)
        {
            super(layout);
            try{
                backGround = ImageIO.read(new File(ChessPiece.filePathHeader+"\\Images\\Menu.png"));
            }catch(IOException e){
            }
        }
        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            g.drawImage(backGround, 0, 0, null);
        }
    }
    //Internal class for handling High Scores. Used for getters and setters.
    class ScoreEntry
    {
        String name, difficulty;
        double score;
        public ScoreEntry(String name, double score, String diff)
        {
            this.name = name;
            this.score = score;
            this.difficulty = diff;
        }
        public String getName()
        {
            return this.name;
        }
        public double getScore()
        {
            return this.score;
        }
        public String getDiff()
        {
            return this.difficulty;
        }
        //Used for printing out the Score Entry into a file.
        @Override
        public String toString()
        {
            return this.name+" "+Double.toString(this.score)+" "+this.difficulty;
        }
    }
    //Sets up the game's Main Menu window.
    public void menu()
    {
        Frame = new JFrame("Knight's Defense");
        Frame.setResizable(false);
        ImagePanel BackgroundPanel = new ImagePanel(new GridLayout(4,3));
        MidPanel = new JPanel(new GridLayout(5,0));
        final JButton[] Options = new JButton[5];
         
        class MenuListener implements ActionListener
        {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource() == Options[0])
                {
                    gameDiff = Difficulty.EASY;
                    scoreMultiplier=1;
                    setupGUI(gameDiff);
                }
                else if(e.getSource() == Options[1])
                {
                    gameDiff = Difficulty.MEDIUM;
                    scoreMultiplier=1.5;
                    setupGUI(gameDiff);
                }
                else if(e.getSource() == Options[2])
                {
                    gameDiff = Difficulty.HARD;
                    scoreMultiplier=2;
                    setupGUI(gameDiff);
                }
                else if(e.getSource() == Options[3])
                    getHelp();
                else
                    displayHighScores();
            }
        }
        MenuListener ML = new MenuListener();
        for(int i=0; i<Options.length;++i)
        {
            Options[i]=new JButton();
            Options[i].setFont(new Font("Serif",Font.ITALIC,12));
            Options[i].addActionListener(ML);
            MidPanel.add(Options[i]);
        }
        Options[0].setText("EASY MODE");
        Options[1].setText("MEDIUM MODE");
        Options[2].setText("HARD MODE");
        Options[3].setText("HOW TO PLAY");
        Options[4].setText("HIGH SCORES");
         
        //Multiple additions of empty JPanels to manipulate the position of the JButtons.
        for(int i=0; i<11; ++i)
        {
            JPanel j = new JPanel();
            j.setVisible(false);
            BackgroundPanel.add(j);
        }
        BackgroundPanel.add(MidPanel);
        Frame.add(BackgroundPanel);
         
        Frame.setPreferredSize(new Dimension(600,580));
        Frame.pack();
        Frame.setVisible(true);
        Frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public void setupGUI(Difficulty diff)
    {
        Frame = new JFrame(gameDiff.toString());
        MainPanel = new JPanel(new BorderLayout());
        MidPanel = new JPanel(new GridLayout(Board.CHESSBOARD_ROWS,Board.CHESSBOARD_COLS));
         
        resetGame();                    //setupGUI could be called multiple times, so it's necessary
        Frame.setResizable(false);      //to reset the UI and the back end systems per game.
          
        Tiles = new TileButton[Board.CHESSBOARD_ROWS][Board.CHESSBOARD_COLS];
        gameDiff = diff;
        spawnPawn();
        
        for(int i=0; i<Board.CHESSBOARD_ROWS;++i)
        {
            for(int j=0; j<Board.CHESSBOARD_COLS;++j)
            {
                Tiles[i][j]=new TileButton(i,j);
                //Manipulates the UI Board's colors to resemble the alternating sequence of a real Board.
                if((i%2==0 && j%2==1) || (i%2==1 && j%2==0))
                    Tiles[i][j].setBackground(Color.DARK_GRAY);
                else
                    Tiles[i][j].setBackground(Color.LIGHT_GRAY);
                //Clicking a Tile makes the Knight move to its position. Clicking an invalid Tile
                //results in a loss of a turn.
                Tiles[i][j].addActionListener(new ActionListener(){
 
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(e.getSource() instanceof TileButton)
                        {
                            destination = ((TileButton) (e.getSource())).outputCoordinates();
                            move();
                            updateGUI();
                        }
                    }
                });
                MidPanel.add(Tiles[i][j]);
            }
        }
        updateGUI();            
          
        MainPanel.add(MidPanel,BorderLayout.CENTER);
        Frame.add(MainPanel);
        Frame.setPreferredSize(new Dimension(600,600));
        Frame.pack();
        Frame.setVisible(true);
    }
    //Updates each Tile in the Board UI to show current changes in the Board.
    public void updateGUI()
    {
        for(int i=0;i<Board.CHESSBOARD_ROWS;++i)
            for(int j=0; j<Board.CHESSBOARD_COLS; ++j)
                if(GameBoard.getPieceAt(i, j) instanceof ChessPiece)
                    Tiles[i][j].setImage(GameBoard.getPieceAt(i, j).getImage());
                else
                    Tiles[i][j].clear();
    }
    //Brings up the How to Play window. Basic controls and movements for non-Chess players.
    public void getHelp()
    {
        final int HELP_WIDTH=400, HELP_HEIGHT=400;
        final int HELP_ROWS=5, HELP_COLS=5;
        Frame = new JFrame("How to Play");
        Frame.setPreferredSize(new Dimension(HELP_WIDTH,HELP_HEIGHT));
        Frame.setResizable(false);
        JPanel Main = new JPanel(new BorderLayout());
        JPanel Mid = new JPanel(new GridLayout(HELP_ROWS,HELP_COLS));
        Tiles = new TileButton[5][5];
         
        TheKnight.setRow(2);
        TheKnight.setCol(2);
         
        for(int i=0; i<HELP_ROWS;++i)
        {
            for(int j=0; j<HELP_COLS;++j)
            {
                Tiles[i][j]=new TileButton(i,j);
                if(TheKnight.canMoveHere(i, j))
                    Tiles[i][j].setText("X");               //Marks the spots where the Knight can move.
                if((i%2==0 && j%2==1) || (i%2==1 && j%2==0))
                    Tiles[i][j].setBackground(Color.DARK_GRAY);
                else
                    Tiles[i][j].setBackground(Color.LIGHT_GRAY);
                 
                Mid.add(Tiles[i][j]);
                Tiles[i][j].setEnabled(false);              //Non-interactable Help menu.
            }
        }
        Tiles[2][2].setImage(TheKnight.getImage());
         
        Main.add(new JLabel("Move as the Knight in an \"L\" shape to keep Pawns from promoting!"),
                BorderLayout.NORTH);
        Main.add(Mid,BorderLayout.CENTER);
        Main.add(new JLabel("Promotions' effects and points per Piece depend on the difficulty."),
                BorderLayout.SOUTH);
        Frame.add(Main);
        Frame.pack();
        Frame.setVisible(true);
    }
    //Lists the past players and their ending game information.
    public void displayHighScores()
    {
        Frame = new JFrame("High Scores");
        Frame.setPreferredSize(new Dimension(250,1000));
        Frame.setResizable(false);
        final int HIGH_SCORE_WIDTH = 250, HIGH_SCORE_HEIGHT = 600;
        final int HIGH_SCORE_COLUMNS = 4;
         
        ArrayList<ScoreEntry> top = new ArrayList<ScoreEntry>();
        top = loadTopScores();
         
        MainPanel = new JPanel(new GridLayout(top.size(),HIGH_SCORE_COLUMNS));
        JLabel[] labels = new JLabel[top.size()*HIGH_SCORE_COLUMNS];
        ScoreEntry SE = null;
        for(int i=0; i< labels.length; ++i)
        {
            labels[i]=new JLabel();
            labels[i].setHorizontalAlignment(JLabel.CENTER);
            //First column
            if(i%4==0)
            {
                SE = top.get(i/HIGH_SCORE_COLUMNS);
                labels[i].setText(Integer.toString(i/HIGH_SCORE_COLUMNS+1));
            }
            //Second column
            if(i%4==1)
            {
                labels[i].setText(SE.getName());
                labels[i].setPreferredSize(new Dimension(HIGH_SCORE_WIDTH
                        / HIGH_SCORE_COLUMNS, HIGH_SCORE_HEIGHT / top.size()));
                labels[i].setHorizontalAlignment(JLabel.LEFT);
            }
            //Third column
            if(i%4==2)
                labels[i].setText(Double.toString(SE.getScore()));
            //Fourth column
            if(i%4==3)
                labels[i].setText(SE.getDiff());
            MainPanel.add(labels[i]);
        }
 
        Frame.add(MainPanel);
        Frame.pack();
        Frame.setVisible(true);
    }
    
    //Takes the name of the player and records their game information in a file. Sorts the entries
    //by their score during both file input and output.
    public void updateHighScores()
    {
        final int HIGH_SCORE_LOG_WIDTH = 350;
        final int HIGH_SCORE_LOG_HEIGHT = 100;
        Frame = new JFrame("High Scores Log: "+gameDiff+" MODE");
        Frame.setPreferredSize(new Dimension(HIGH_SCORE_LOG_WIDTH,HIGH_SCORE_LOG_HEIGHT));
        Frame.setResizable(false);
         
        MainPanel = new JPanel(new BorderLayout());
        JPanel Top = new JPanel(new GridLayout(0,2));
        JPanel Mid = new JPanel(new GridLayout(0,2));
        JPanel Bot = new JPanel();
         
        JLabel NameLabel = new JLabel("Name: ");
        JLabel ScoreLabel = new JLabel("Score: ");
        final JTextField Name = new JTextField();
        JTextField Score = new JTextField(playerScore+"");
        Name.setPreferredSize(new Dimension(100,20));
        Score.setPreferredSize(new Dimension(50,20));
        Score.setEditable(false);
        JButton OK = new JButton("OK");
         
        OK.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                if(endgame)
                {
                    String playerName = Name.getText().isEmpty()? "Unknown_Knight": Name.getText();
                    playerName = playerName.replace(' ', '_');      //Spaces will throw off the file reading
                    ScoreEntry entry = new ScoreEntry(playerName,playerScore,gameDiff.toString());
                    ArrayList<ScoreEntry> entries = loadTopScores();
                    entries.add(entry);
                    saveScores(entries);
                    resetGame();
                    Frame.dispose();
                }
            }
        });
         
        Top.add(NameLabel); Top.add(Name); 
        Mid.add(ScoreLabel); Mid.add(Score); 
        Bot.add(OK);
        MainPanel.add(Top, BorderLayout.NORTH);
        MainPanel.add(Mid, BorderLayout.CENTER);
        MainPanel.add(Bot, BorderLayout.SOUTH);
        Frame.add(MainPanel);
        Frame.pack();
        Frame.setVisible(true);
    }
    //Loads the file information into an ArrayList of ScoreEntry. Sorts by score.
    public ArrayList<ScoreEntry> loadTopScores()
    {
        ArrayList<ScoreEntry> entries = new ArrayList<ScoreEntry>();
        try {
            Scanner reader = new Scanner(new File("HighScores.txt"));
            ScoreEntry E = null;
            while(reader.hasNext())
            {
                String name = reader.next();
                double score = reader.nextDouble();
                String diff = reader.next();
                E = new ScoreEntry(name,score,diff);
                entries.add(E);
            }
            entries.sort(new Comparator<ScoreEntry>(){
                @Override
                public int compare(ScoreEntry A, ScoreEntry B)
                {
                    return Double.compare(((ScoreEntry) B).getScore(),((ScoreEntry) A).getScore());
                }
            });
            reader.close();
        } catch (FileNotFoundException EX){
            JOptionPane.showMessageDialog(Frame, "Input file not found.");
        }
        return entries;
    }
    
    //Saves players' information from an ArrayList of ScoreEntry into a file. Sorts by score.
    public void saveScores(ArrayList<ScoreEntry> scores)
    {
        if(!scores.isEmpty())
        {
            scores.sort(new Comparator<ScoreEntry>(){
            @Override
            public int compare(ScoreEntry A, ScoreEntry B){
                return Double.compare(((ScoreEntry) B).getScore(),((ScoreEntry) A).getScore());
            }
            });
            try{
                PrintWriter writer = new PrintWriter("HighScores.txt");
                for(ScoreEntry SE: scores)
                    writer.println(SE);
                writer.close();
            }catch(FileNotFoundException EX){
                JOptionPane.showMessageDialog(Frame, "Output file not found.");
            }
        }
    }
    
    //Only spawns the Knight within the Board's lower half.
    public void spawnKnight()
    {
        int rand1 = (int) (Math.random() * (Board.CHESSBOARD_ROWS - (Board.CHESSBOARD_ROWS / 2)))
                + (Board.CHESSBOARD_ROWS / 2);
        int rand2 = (int)(Math.random()*Board.CHESSBOARD_ROWS);
        TheKnight = new Knight(GameBoard, rand1, rand2);
        GameBoard.add(TheKnight, TheKnight.getRow(), TheKnight.getCol());
    }
    
    //Spawns the Pawns from the Board's top row. Pawns have a 50% chance of spawning anywhere
    //in the top row per turn.
    public void spawnPawn()
    {
        final int chance = 50;
        int rand = (int)(Math.random()*100);
        if(rand >= (100-chance))
        {
            int whichColumn = (int)(Math.random()*Board.CHESSBOARD_COLS); //Calculates the random column the pawn spawns.
            int topRow = 0;
            
            Pawn P = new Pawn(GameBoard, topRow, whichColumn);
            
            GameBoard.add(P, P.getRow(), P.getCol());
            pieces.add(P);
        }
    }
    
    //Checks whether the Knight still exists in the current Board.
    public boolean knightAlive()
    {
        boolean alive = pieces.contains(TheKnight);
        if(!alive)
            endgame=true;
        return alive;
    }
    
    //Cleans up all the pieces in the list with negative coordinates;
    //Negative coordinates means the ChessPiece has been captured.
    public void cleanupPieces(ArrayList<ChessPiece> list)
    {
    	for(int i=0; i<list.size(); i+=1)
    		if(list.get(i).getCol() < 0 && list.get(i).getRow() < 0)
    			list.remove(i);
    	
    	/*Locks the subsequent ChessPieces in place.
        if(pieces.get(i).getRow()==-1 || pieces.get(i).getCol()==-1)
        {
            pieces.remove(i);
            continue;                                         
        }*/
    }
    
    //Prompts the Knight to move, followed by every ChessPiece's individual movement.
    public void move()
    {
        boolean pieceMoved = false;
        
        for(int i=0; i<pieces.size(); ++i)
        {
        	cleanupPieces(pieces);
             
            if(!endgame && knightAlive())                       //Checks if the game has ended.
            {
                //If there are no ChessPieces on the Board to call move() on;
            	try
                {
                	pieceMoved = pieces.get(i).move();
                	updateGUI();
                }
                catch(IndexOutOfBoundsException IOOBE)
                {
                	return;
                }
                
                if(pieces.get(i) instanceof Knight)             //Calculates the points in the Knight's turn
                {
                    double add = 0, multiplier = 1;
                    ChessPiece target = GameBoard.getPieceAt(destination.row(), destination.col());
                    multiplier = target instanceof ChessPiece && !target.getSide()? scoreMultiplier:1;
                    add = ((Knight)pieces.get(i)).move(destination);
                    playerScore += (add*multiplier);
                }
                if (!pieceMoved && pieces.get(i) instanceof Pawn && pieces.get(i).getRow() == Board.CHESSBOARD_ROWS - 1) //Pawn hasn't moved and it's at the end of the Board. Pawn Promotion.
                {
                    Pawn P = (Pawn) pieces.get(i);
                    
                    ChessPiece newPiece = null;
                    
                    if(gameDiff == Difficulty.HARD)         //No Promotions allowed in Hard Mode.
                    {
                        endgame = true;
                        updateGUI();
                        return;
                    }
                    else if(gameDiff == Difficulty.MEDIUM)  //Medium difficulty Promotes Pawns to Queens.
                    {
                        newPiece = P.promote(ChessPiece.PromoteType.QUEEN);
                    }
                    else if(gameDiff == Difficulty.EASY)    //Easy difficulty provides a 50% chance of
                    {                                       //Rook Promotion, 50% of Bishop Promotion
                        int rand = (int)(Math.random()*100);
                        if(rand >= 49)
                            newPiece = P.promote(ChessPiece.PromoteType.ROOK);
                        else
                            newPiece = P.promote(ChessPiece.PromoteType.BISHOP);
                    }
                    pieces.add(newPiece);
                    pieces.remove(P);
                    updateGUI();
                    
                    return;
                }
            }
            else
            {
                updateHighScores();
                return;
            }
        }
        spawnPawn();
    }
    

    public static void main(String[] args)
    {
        KnightsDefense KD = new KnightsDefense();
        KD.menu();
    }
}