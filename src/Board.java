import java.util.ArrayList;
   
public class Board 
{
    final static int CHESSBOARD_ROWS = 8;
    final static int CHESSBOARD_COLS = 8;
    ArrayList<ArrayList<Object>> ChessBoard;
     
    //Fills the 2D ArrayList with entities of type Object  
    public Board()
    {
        ChessBoard = new ArrayList<ArrayList<Object>>();
        for(int i=0; i<Board.CHESSBOARD_ROWS; ++i)
        {
            ChessBoard.add(new ArrayList<Object>());
            for(int j=0; j<Board.CHESSBOARD_COLS; ++j)
               ChessBoard.get(i).add(new Object());
        }
    }
    public boolean isValid(int r, int c)
    {
        if(r >= CHESSBOARD_ROWS || r < 0)
            return false;
        else if(c >= CHESSBOARD_COLS || c < 0)
            return false;
        else
            return true;
    }
    public boolean isAvailable(int r, int c)
    {
        if(isValid(r,c))
        {
            if(ChessBoard.get(r).get(c) instanceof ChessPiece)
                return false;
            return true;
        }
        return false;
    }
    //Used to pinpoint a specific ChessPiece on the Board for numerous other methods.
    public ChessPiece getPieceAt(int row, int col) 
    {
        if(!this.isAvailable(row, col))
            return (ChessPiece)(ChessBoard.get(row).get(col));
        return null;
    }
    public boolean add(ChessPiece P, int row, int col) 
    {
        if(this.isValid(row, col))
        {   
            ChessBoard.get(row).remove(col);   
            ChessBoard.get(row).add(col, P);   
            return true;                    
        }
        return false;
    }
    //Original moving method for the ChessPieces. The ChessPieces feed information into the Board,
    //which then does the "heavy lifting" in ChessPiece movement.
    public boolean moveFromTo(ChessPiece P, int curRow, int curCol, int destRow,int destCol) 
    {
        if(this.isValid(destRow, destCol) && !this.isAvailable(curRow, curCol))
        {
            if(this.getPieceAt(destRow, destCol) instanceof ChessPiece)
            {
                this.getPieceAt(destRow, destCol).setRow(-1);
                this.getPieceAt(destRow, destCol).setCol(-1);
            }
             
            P=this.getPieceAt(curRow, curCol);
            ChessBoard.get(curRow).remove(curCol);             
            ChessBoard.get(curRow).add(curCol, new Object());  
                                                              
            this.add(P, destRow, destCol);
            P.setRow(destRow);
            P.setCol(destCol);
            return true;
        }
        return false;
    }
    //Allows all pieces to know the Knight's Coordinates at all times. Also used to check
    //if the Knight still exists in the Board.
    public Knight findKnight()
    {
        Knight target = null;
        for(int i=0;i<Board.CHESSBOARD_ROWS;++i)
            for(int j=0;j<Board.CHESSBOARD_COLS;++j)
                if(ChessBoard.get(i).get(j) instanceof Knight)
                    target = (Knight)ChessBoard.get(i).get(j);
        return target;
    }
}