import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
 
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
  
public class ChessPiece 
{
    Coordinates coords;
    boolean side;
    Board MyBoard;
    Image img;
    String imagePath;
    final static int BISHOP_BOUNTY = 3, ROOK_BOUNTY = 5, PAWN_BOUNTY = 1, QUEEN_BOUNTY = 9;
    static String filePathHeader = "E:\\Programming IDE\'s\\Eclipse\\eclipse\\workspace\\KnightsDefense";
    int bounty;
      
    static boolean exclusiveOr(boolean left, boolean right)
    {
        return (left || right) && !(left && right);
    }
    public ChessPiece(Board theBoard, int row, int col)
    {
        MyBoard = theBoard;
        this.coords = new Coordinates(row,col);
    }
    public enum PromoteType
    {
        QUEEN, ROOK, BISHOP;
    }
    //This method checks if the vertical path to a given row is clear of obstructions.
    public boolean verticalPathClear(int row)
    {
        if(this.getRow() < row)
        {
            for(int i=this.getRow()+1; i<this.getRow()+Math.abs(this.getRow()-row); ++i)
                if(MyBoard.getPieceAt(i, this.getCol()) instanceof ChessPiece)
                    return false;
            return true;
        }
        else if(this.getRow() > row)
        {
            for(int i=this.getRow()-1; i>this.getRow()-Math.abs(this.getRow()-row);--i)
                if(MyBoard.getPieceAt(i, this.getCol()) instanceof ChessPiece)
                    return false;
            return true;
        }
        else
            return false;
    }
    //Similar to verticalPathClear, but checks for horizontal obstructions to a given column.
    public boolean horizontalPathClear(int col)
    {
        if(this.getCol() < col)
        {
            for(int i=this.getCol()+1;i<this.getCol()+Math.abs(this.getCol()-col);++i)
                if(MyBoard.getPieceAt(this.getRow(), i) instanceof ChessPiece)
                    return false;
            return true;
        }
        else if(this.getCol() > col)
        {
            for(int i=this.getCol()-1;i>this.getCol()-Math.abs(this.getCol()-col);--i)
                if(MyBoard.getPieceAt(this.getRow(), i) instanceof ChessPiece)
                    return false;
            return true;
        }
        else
            return false;
    }
    //This method checks whether the diagonal path to a given Coordinates is unobstructed.
    public boolean diagonalPathClear(int row, int col)
    {
        if(!MyBoard.isValid(row, col))
            return false;
        if(this.getRow() > row && this.getCol() > col)//NW
        {
            for(int i=this.getRow()-1, j=this.getCol()-1;(i>row && j>col);--i,--j)
                if(MyBoard.getPieceAt(i, j) instanceof ChessPiece)
                    return false;
            return true;
        }
        else if(this.getRow() < row && this.getCol() < col)//SE
        {
            for(int i=this.getRow()+1, j=this.getCol()+1;(i<row && j<col);++i,++j)
                if(MyBoard.getPieceAt(i, j) instanceof ChessPiece)
                    return false;
            return true;
        }
        else if(this.getRow() > row && this.getCol() < col)//NE
        {
            for(int i=this.getRow()-1, j=this.getCol()+1;(i>row && j<col);--i,++j)
                if(MyBoard.getPieceAt(i, j) instanceof ChessPiece)
                    return false;
            return true;
        }
        else if(this.getRow() < row && this.getCol() > col)//SW
        {
            for(int i=this.getRow()+1, j=this.getCol()-1;(i<row && j>col);++i,--j)
                if(MyBoard.getPieceAt(i, j) instanceof ChessPiece)
                    return false;
            return true;
        }
        else
            return false;
    }
    //Gets all Coordinates that is diagonal to a ChessPiece's own Coordinates. The diagonals include 
    //a ChessPiece's own Coordinates. Heavily used for Bishop and Queen target systems.
    public ArrayList<Coordinates> getDiagonals()
    {
        ArrayList<Coordinates> list = new ArrayList<Coordinates>();
        for(int i=0; i<Board.CHESSBOARD_ROWS;++i)
            for(int j=0; j<Board.CHESSBOARD_COLS; ++j)
                if(Math.abs(i-this.getRow()) == Math.abs(j-this.getCol()))
                {
                    Coordinates c = new Coordinates(i,j);
                    list.add(c);
                }
        return list;
    }
    //Getters and setters below:
    public int getRow()
    {
        return this.coords.row();
    }
    public int getCol()
    {
        return this.coords.col();
    }
    public boolean getSide()
    {
        return this.side;
    }
    public void setRow(int row)
    {
        this.coords.setRow(row);
    }
    public void setCol(int col)
    {
        this.coords.setCol(col);
    }
    public boolean canMoveHere(int row, int col)
    {
        return false;
    }
    public boolean move()//Override, not used.
    {
        return false;
    }
    public Image getImage()
    {
        return this.img;
    }
    public void setFilePath(String path)
    {
        filePathHeader = path;
    }
    public void setImagePath(String imagePath)
    {
        this.imagePath = imagePath;
        try{
            img = ImageIO.read(new File(this.imagePath));
            img = img.getScaledInstance(75, 75, Image.SCALE_DEFAULT);
        }
        catch(IOException e){
            JOptionPane.showMessageDialog(null, "Image not found in folders! Use ChessPiece.setFilePath(String arg0)");
        }
         
    }
    public int getBounty()
    {
        return this.bounty;
    }
}