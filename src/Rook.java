public class Rook extends ChessPiece
{
    public Rook(Board theBoard, int row, int col) 
    {
        super(theBoard, row, col);
        side=false;
        bounty = ChessPiece.ROOK_BOUNTY;
        this.imagePath = ChessPiece.filePathHeader+"\\Images\\Rook.png";
        super.setImagePath(this.imagePath);
    }
    @Override
    public boolean canMoveHere(int row, int col)
    {
        boolean A = this.getRow()==row && horizontalPathClear(col);
        boolean B = this.getCol()==col && verticalPathClear(row);
        boolean D = MyBoard.getPieceAt(row, col) instanceof ChessPiece ? MyBoard
                .getPieceAt(row, col).getSide() : true;
                  
        return (exclusiveOr(A,B)&&D);
    }
    @Override
    public boolean move()
    {
        int kr=0, kc=0;
        try{
            kr = MyBoard.findKnight().getRow();
            kc = MyBoard.findKnight().getCol();
        }catch(NullPointerException e){
        }
         
        if(this.canMoveHere(kr,kc))                             //Capturing the Knight is first priority.
        {
            MyBoard.moveFromTo(this, this.getRow(), this.getCol(), kr, kc);
            return true;
        }
        else
        {
            //The Rook can "check" the Knight after either a vertical or a horizontal move.
            if(this.canMoveHere(this.getRow(), kc) && this.canMoveHere(kr,this.getCol()))
            {
                int rand = (int)((Math.random()*100)+1);
                if(rand>50)
                    MyBoard.moveFromTo(this, this.getRow(), this.getCol(), this.getRow(), kc);
                else
                    MyBoard.moveFromTo(this, this.getRow(), this.getCol(), kr, this.getCol());
                return true;
            }
            else if(this.canMoveHere(kr,this.getCol()))
            {
                MyBoard.moveFromTo(this, this.getRow(), this.getCol(), kr, this.getCol());
                return true;                        //The Rook "checks" the Knight after a vertical move.
            }
            else if(this.canMoveHere(this.getRow(), kc))
            {
                MyBoard.moveFromTo(this, this.getRow(), this.getCol(), this.getRow(), kc);
                return true;                        //The Rook "checks" the Knight after a horizontal move.
            }
            return false;
        }
    }   
}