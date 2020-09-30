public class Pawn extends ChessPiece
{
    public Pawn(Board theBoard, int row, int col) 
    {
        super(theBoard, row, col);
        side = false;
        bounty = ChessPiece.PAWN_BOUNTY;
        this.imagePath = ChessPiece.filePathHeader+"\\Images\\Pawn.png";
        super.setImagePath(this.imagePath);
    }
    //A method unique to the Pawn. Allows the Pawn to promote into an "officer" ChessPiece.
    public ChessPiece promote(PromoteType type)
    {
        ChessPiece promote = null;
        if(type == PromoteType.QUEEN)
            promote = new Queen(MyBoard,this.getRow(),this.getCol());
        else if(type == PromoteType.ROOK)
            promote = new Rook(MyBoard, this.getRow(), this.getCol());
        else if(type == PromoteType.BISHOP)
            promote = new Bishop(MyBoard, this.getRow(),this.getCol());
           
        MyBoard.add(promote, this.getRow(), this.getCol());
        return promote;
    }
    @Override
    public boolean canMoveHere(int row, int col)
    {
        boolean A = this.getRow()-row==-1;
        boolean B = (MyBoard.getPieceAt(row, col) instanceof Knight);
        boolean C = Math.abs(this.getCol()-col)==1;
        boolean D = this.getCol()==col;
        boolean E = MyBoard.isAvailable(row, col);
          
        if((A&&B&&C))
            return true;
        else if(A && D && E)
            return true;
        else
            return false;
    }
    @Override
    public boolean move()
    {
        boolean bool = false;
        int KR = 0, KC = 0;
        try{
            KR = MyBoard.findKnight().getRow();
            KC = MyBoard.findKnight().getCol();
        }catch(NullPointerException e){
        }
         
        if(this.getRow() == Board.CHESSBOARD_ROWS-1)
            bool = false;                               //The Pawn has reached the end of the Board.
        else if(this.canMoveHere(KR, KC))
        {
            MyBoard.moveFromTo(this, this.getRow(), this.getCol(), KR, KC);
            bool = true;                                //The Pawn can capture the Knight.
        }
        else if(this.canMoveHere(this.getRow()+1,this.getCol()))
        {
            MyBoard.moveFromTo(this, this.getRow(), this.getCol(), this.getRow()+1, this.getCol());
            bool = true;                                //The Pawn's forward path is unobstructed.
        }
        return bool;
    }
}