import java.util.stream.Collectors;
import java.util.*;

abstract class Piece
{
  protected Position position;
  private Board board;

  public Piece (Board board)
  {
    this.board = board;
    position = null;
  }

  protected Board getBoard ()
  {
    return board;
  }

  public abstract boolean[][] possibleMoves ();

  public boolean possibleMove (Position position)
  {
    return possibleMoves ()[position.getRow ()][position.getColumn ()];
  }

  public boolean isThereAnyPossibleMove ()
  {
    boolean[][]mat = possibleMoves ();
    for (int i = 0; i < mat.length; i++)
      {
	for (int j = 0; j < mat.length; j++)
	  {
	    if (mat[i][j])
	      {
		return true;
	      }
	  }
      }
    return false;
  }
}

class Position
{
  private int row;
  private int column;

  public Position (int row, int column)
  {
    this.row = row;
    this.column = column;
  }
  public int getRow ()
  { 
    return row;
  }
  public void setRow (int row)
  {
    this.row = row;
  }
  public int getColumn ()
  {
    return column;
  }
  public void setColumn (int column)
  {
    this.column = column;
  }
  public void setValues (int row, int column)
  {
    this.row = row;
    this.column = column;
  }

  @Override public String toString ()
  {
    return row + ", " + column;
  }
}

class Board
{
  private Integer rows;
  private Integer columns;
  private Piece[][] pieces;

  public Board (int rows, int columns)
  {
    if (rows < 1 || columns < 1)
      {
	throw new BoardException ("invalid rows and cols");
      }
    this.rows = rows;
    this.columns = columns;
    pieces = new Piece[rows][columns];
  }

  public Integer getRows ()
  {
    return rows;
  }

  public Integer getColumns ()
  {
    return columns;
  }

  public Piece piece (Integer row, Integer column)
  {
    if (!positionExists (row, column))
      {
	throw new BoardException ("invalid position");
      }
    return pieces[row][column];
  }

  public Piece piece (Position position)
  {
    if (!positionExists (position))
      {
	throw new BoardException ("invalid position");
      }
    return pieces[position.getRow ()][position.getColumn ()];
  }

  public void placePiece (Piece piece, Position position)
  {
    if (thereIsAPiece (position))
      {
	throw new BoardException ("select another empty position");
      }
    pieces[position.getRow ()][position.getColumn ()] = piece;
    piece.position = position;
  }

  public Piece removePiece (Position position)
  {
    if (!positionExists (position))
      {
	throw new BoardException ("empty position");
      }
    if (piece (position) == null)
      {
	return null;
      }
    Piece aux = piece (position);
    aux.position = null;
    pieces[position.getRow ()][position.getColumn ()] = null;
    return aux;
  }

  private boolean positionExists (int row, int column)
  {
    return row >= 0 && row < rows && column >= 0 && column < columns;
  }

  public boolean positionExists (Position position)
  {
    return positionExists (position.getRow (), position.getColumn ());
  }

  public boolean thereIsAPiece (Position position)
  {
    if (!positionExists (position))
      {
	throw new BoardException ("empty position");
      }
    return piece (position) != null;
  }
}

class BoardException extends RuntimeException
{
  private static final long serialVersionUID = 1L;

  public BoardException (String msg)
  {
    super (msg);
  }
}

enum Color
{
  BLACK,
  WHITE;
}

class ChessException extends BoardException
{
  private static final long serialVersionUID = 1L;

  public ChessException (String msg)
  {
    super (msg);
  }
}

abstract class ChessPiece extends Piece
{

  private Color color;
  private int moveCount;

  public ChessPiece (Board board, Color color)
  {
    super (board);
    this.color = color;
  }

  public Color getColor ()
  {
    return color;
  }

  public int getMoveCount ()
  {
    return moveCount;
  }

  public void increaseMoveCount ()
  {
    moveCount++;
  }
  public void decreaseMoveCount ()
  {
    moveCount--;
  }

  protected boolean isThereOpponentPiece (Position position)
  {
    ChessPiece p = (ChessPiece) getBoard ().piece (position);
    return p != null && p.getColor () != color;
  }

  public ChessPosition getChessPosition ()
  {
    return ChessPosition.fromPosition (position);
  }
}

class ChessPosition
{

  private char column;
  private Integer row;

  public ChessPosition (char column, Integer row)
  {
    if (column < 'a' || column > 'h' || row < 1 || row > 8)
      {
	throw new
	  ChessException
	  ("Erro ao instanciar a posiC'C#o no tabuleiro. Valores validos sC#o de a1 atC) h8.");
      }
    this.column = column;
    this.row = row;
  }

  public char getColumn ()
  {
    return column;
  }

  public Integer getRow ()
  {
    return row;
  }

  protected Position toPosition ()
  {
    return new Position (8 - row, column - 'a');
  }

  protected static ChessPosition fromPosition (Position position)
  {
    return new ChessPosition ((char) ('a' + position.getColumn ()),
			      8 - position.getRow ());
  }

  @Override public String toString ()
  {
    return "" + column + row;
  }

}

class ChessMatch
{
  private ChessPiece enPassantVulnerable;
  private ChessPiece promoted;
  private Board board;
  private Integer turn;
  private Color currentPlayer;
  private boolean check;
  private boolean checkMate;

  private List < Piece > piecesOnTheBoard = new ArrayList <> ();
  private List < Piece > capturedPieces = new ArrayList <> ();

  public Integer getTurn ()
  {
    return turn;
  }

  public Color getCurrentPlayer ()
  {
    return currentPlayer;
  }

  public boolean isCheck ()
  {
    return check;
  }

  public boolean isCheckMate ()
  {
    return checkMate;
  }

  public ChessPiece getEnPassantVulnerable ()
  {
    return enPassantVulnerable;
  }

  public ChessPiece getPromoted ()
  {
    return promoted;
  }

  public ChessMatch ()
  {
    this.board = new Board (8, 8);
    turn = 1;
    currentPlayer = Color.WHITE;
    initialSetup ();
  }

  public ChessPiece[][] getPieces ()
  {
    ChessPiece[][]mat = new ChessPiece[board.getRows ()][board.getColumns ()];
    for (int i = 0; i < board.getRows (); i++)
      {
	for (int j = 0; j < board.getColumns (); j++)
	  {
	    mat[i][j] = (ChessPiece) board.piece (i, j);
	  }
      }
    return mat;
  }



  public ChessPiece performChessMove (ChessPosition sourcePosition,
				      ChessPosition targetPosition)
  {
    Position source = sourcePosition.toPosition ();
    Position target = targetPosition.toPosition ();
    validateSourcePosition (source);
    validateTargetPosition (source, target);
    Piece capturedPiece = makeMove (source, target);
    if (testCheck (currentPlayer))
      {
	undoMove (source, target, capturedPiece);
	throw new ChessException ("undo move");
      }
    ChessPiece movedPiece = (ChessPiece) board.piece (target);

    //#specialmove promotion
    promoted = null;
    if (movedPiece instanceof Pawn)
      {
	if ((movedPiece.getColor () == Color.WHITE && target.getRow () == 0)
	    || (movedPiece.getColor () == Color.BLACK
		&& target.getRow () == 7))
	  {
	    promoted = (ChessPiece) board.piece (target);
	    promoted = replacepromotedPiece ("A");
	  }
      }

    check = (testCheck (opponent (currentPlayer))) ? true : false;

    if (testCheckMate (opponent (currentPlayer)))
      {
	checkMate = true;
      }
    else
      {
	nextTurn ();
      }

    // #specialmove en passant
    if (movedPiece instanceof Pawn
	&& (target.getRow () == source.getRow () - 2
	    || target.getRow () == source.getRow () + 2))
      {
	enPassantVulnerable = movedPiece;
      }
    else
      {
	enPassantVulnerable = null;
      }

    return (ChessPiece) capturedPiece;
  }

  public ChessPiece replacepromotedPiece (String type)
  {
    if (promoted == null)
      {
	throw new IllegalStateException ("null promoted");
      }
    if (!type.equals ("T") && !type.equals ("A") && !type.equals ("C")
	&& !type.equals ("B"))
      {

      }
    Position position = promoted.getChessPosition ().toPosition ();
    Piece p = board.removePiece (position);
    piecesOnTheBoard.remove (p);

    ChessPiece newPiece = newPiece (type, promoted.getColor ());
    board.placePiece (newPiece, position);
    piecesOnTheBoard.add (newPiece);

    return newPiece;
  }

  private ChessPiece newPiece (String type, Color color)
  {
    if (type.equals ("B"))
      return new Bishop (board, color);
    if (type.equals ("C"))
      return new Knight (board, color);
    if (type.equals ("A"))
      return new Queen (board, color);
    return new Rook (board, color);
  }

  private void validateSourcePosition (Position position)
  {
    if (!board.thereIsAPiece (position))
      {
	throw new ChessException ("there Is no Piece.");
      }
    if (currentPlayer != ((ChessPiece) board.piece (position)).getColor ())
      {
	throw new ChessException ("The piece chosen is not yours.");
      }
    if (!board.piece (position).isThereAnyPossibleMove ())
      {
	throw new
	  ChessException ("There are no moves possible for the chosen piece");
      }
  }

  public boolean[][] possibleMoves (ChessPosition sourcePosition)
  {
    Position position = sourcePosition.toPosition ();
    validateSourcePosition (position);
    return board.piece (position).possibleMoves ();
  }

  private Piece makeMove (Position source, Position target)
  {
    ChessPiece p = (ChessPiece) board.removePiece (source);
    p.increaseMoveCount ();
    Piece capturedPiece = board.removePiece (target);
    board.placePiece (p, target);
    if (capturedPiece != null)
      {
	piecesOnTheBoard.remove (capturedPiece);
	capturedPieces.add (capturedPiece);
      }

    //#Special move castling king side rook
    if (p instanceof King && target.getColumn () == source.getColumn () + 2)
      {
	Position sourceT =
	  new Position (source.getRow (), source.getColumn () + 3);
	Position targetT =
	  new Position (source.getRow (), source.getColumn () + 1);
	ChessPiece rook = (ChessPiece) board.removePiece (sourceT);
	board.placePiece (rook, targetT);
	rook.increaseMoveCount ();
      }
    //#Special move castling king side rook
    else if (p instanceof King
	     && target.getColumn () == source.getColumn () - 2)
      {
	Position sourceT =
	  new Position (source.getRow (), source.getColumn () - 4);
	Position targetT =
	  new Position (source.getRow (), source.getColumn () - 1);
	ChessPiece rook = (ChessPiece) board.removePiece (sourceT);
	board.placePiece (rook, targetT);
	rook.increaseMoveCount ();
      }
    // #specialmove en passant
    if (p instanceof Pawn)
      {
	if (source.getColumn () != target.getColumn ()
	    && capturedPiece == null)
	  {
	    Position pawnPosition;
	    if (p.getColor () == Color.WHITE)
	      {
		pawnPosition =
		  new Position (target.getRow () + 1, target.getColumn ());
	      }
	    else
	      {
		pawnPosition =
		  new Position (target.getRow () - 1, target.getColumn ());
	      }
	    capturedPiece = board.removePiece (pawnPosition);
	    capturedPieces.add (capturedPiece);
	    piecesOnTheBoard.remove (capturedPiece);
	  }
      }

    return capturedPiece;
  }

  private void undoMove (Position source, Position target, Piece captured)
  {
    ChessPiece p = (ChessPiece) board.removePiece (target);
    p.decreaseMoveCount ();
    board.placePiece (p, source);

    if (captured != null)
      {
	board.placePiece (captured, target);
	capturedPieces.remove (captured);
	piecesOnTheBoard.add (captured);
      }
    //#Special move castling king side rook
    if (p instanceof King && target.getColumn () == source.getColumn () + 2)
      {
	Position sourceT =
	  new Position (source.getRow (), source.getColumn () + 3);
	Position targetT =
	  new Position (source.getRow (), source.getColumn () + 1);
	ChessPiece rook = (ChessPiece) board.removePiece (targetT);
	board.placePiece (rook, sourceT);
	rook.decreaseMoveCount ();
      }
    //#Special move castling king side rook
    else if (p instanceof King
	     && target.getColumn () == source.getColumn () - 2)
      {
	Position sourceT =
	  new Position (source.getRow (), source.getColumn () - 4);
	Position targetT =
	  new Position (source.getRow (), source.getColumn () - 1);
	ChessPiece rook = (ChessPiece) board.removePiece (targetT);
	board.placePiece (rook, sourceT);
	rook.decreaseMoveCount ();
      }

    // #specialmove en passant
    if (p instanceof Pawn)
      {
	if (source.getColumn () != target.getColumn ()
	    && captured == enPassantVulnerable)
	  {
	    ChessPiece pawn = (ChessPiece) board.removePiece (target);
	    Position pawnPosition;
	    if (p.getColor () == Color.WHITE)
	      {
		pawnPosition = new Position (3, target.getColumn ());
	      }
	    else
	      {
		pawnPosition = new Position (4, target.getColumn ());
	      }
	    board.placePiece (pawn, pawnPosition);
	  }
      }
  }

  private void validateTargetPosition (Position source, Position target)
  {
    if (!board.piece (source).possibleMove (target))
      {
	throw new
	  ChessException
	  ("The chosen piece cannot move to the chosen position");
      }
  }

  private void nextTurn ()
  {
    turn++;
    currentPlayer =
      (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
  }

  private void placeNewPiece (char column, int row, ChessPiece piece)
  {
    board.placePiece (piece, new ChessPosition (column, row).toPosition ());
    piecesOnTheBoard.add (piece);
  }

  private Color opponent (Color color)
  {
    return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
  }

  private ChessPiece king (Color color)
  {
    List < Piece > list = listColorPieces (color);
  for (Piece p:list)
      {
	if (p instanceof King)
	  {
	    return (ChessPiece) p;
	  }
      }
    throw new IllegalStateException ("There is no king with the color " +
				     color);
  }

  private List < Piece > listColorPieces (Color color)
  {
    return piecesOnTheBoard.stream ().filter (x->((ChessPiece) x).
					      getColor () ==
					      color).collect (Collectors.
							      toList ());
  }

  private boolean testCheck (Color color)
  {
    Position kingPosition = king (color).getChessPosition ().toPosition ();
    List < Piece > opponentPieces = listColorPieces (opponent (color));

  for (Piece p:opponentPieces)
      {
	boolean[][]mat = p.possibleMoves ();
	if (mat[kingPosition.getRow ()][kingPosition.getColumn ()])
	  {
	    return true;
	  }
      }
    return false;
  }

  private boolean testCheckMate (Color color)
  {
    if (!testCheck (color))
      {
	return false;
      }
    List < Piece > list = listColorPieces (color);
  for (Piece p:list)
      {
	boolean[][]mat = p.possibleMoves ();
	for (int i = 0; i < board.getRows (); i++)
	  {
	    for (int j = 0; j < board.getColumns (); j++)
	      {
		if (mat[i][j])
		  {
		    Position source =
		      ((ChessPiece) p).getChessPosition ().toPosition ();
		    Position target = new Position (i, j);
		    Piece capturedPiece = makeMove (source, target);
		    boolean testCheck = testCheck (color);
		    undoMove (source, target, capturedPiece);
		    if (!testCheck)
		      {
			return false;
		      }
		  }
	      }
	  }
      }
    return true;

  }

  private void initialSetup ()
  {
    placeNewPiece ('a', 1, new Rook (board, Color.WHITE));
    placeNewPiece ('b', 1, new Knight (board, Color.WHITE));
    placeNewPiece ('c', 1, new Bishop (board, Color.WHITE));
    placeNewPiece ('d', 1, new Queen (board, Color.WHITE));
    placeNewPiece ('e', 1, new King (board, Color.WHITE, this));
    placeNewPiece ('f', 1, new Bishop (board, Color.WHITE));
    placeNewPiece ('g', 1, new Knight (board, Color.WHITE));
    placeNewPiece ('h', 1, new Rook (board, Color.WHITE));
    placeNewPiece ('a', 2, new Pawn (board, Color.WHITE, this));
    placeNewPiece ('b', 2, new Pawn (board, Color.WHITE, this));
    placeNewPiece ('c', 2, new Pawn (board, Color.WHITE, this));
    placeNewPiece ('d', 2, new Pawn (board, Color.WHITE, this));
    placeNewPiece ('e', 2, new Pawn (board, Color.WHITE, this));
    placeNewPiece ('f', 2, new Pawn (board, Color.WHITE, this));
    placeNewPiece ('g', 2, new Pawn (board, Color.WHITE, this));
    placeNewPiece ('h', 2, new Pawn (board, Color.WHITE, this));

    placeNewPiece ('a', 8, new Rook (board, Color.BLACK));
    placeNewPiece ('b', 8, new Knight (board, Color.BLACK));
    placeNewPiece ('c', 8, new Bishop (board, Color.BLACK));
    placeNewPiece ('d', 8, new Queen (board, Color.BLACK));
    placeNewPiece ('e', 8, new King (board, Color.BLACK, this));
    placeNewPiece ('f', 8, new Bishop (board, Color.BLACK));
    placeNewPiece ('g', 8, new Knight (board, Color.BLACK));
    placeNewPiece ('h', 8, new Rook (board, Color.BLACK));
    placeNewPiece ('a', 7, new Pawn (board, Color.BLACK, this));
    placeNewPiece ('b', 7, new Pawn (board, Color.BLACK, this));
    placeNewPiece ('c', 7, new Pawn (board, Color.BLACK, this));
    placeNewPiece ('d', 7, new Pawn (board, Color.BLACK, this));
    placeNewPiece ('e', 7, new Pawn (board, Color.BLACK, this));
    placeNewPiece ('f', 7, new Pawn (board, Color.BLACK, this));
    placeNewPiece ('g', 7, new Pawn (board, Color.BLACK, this));
    placeNewPiece ('h', 7, new Pawn (board, Color.BLACK, this));
  }
}


class Bishop extends ChessPiece
{
  public Bishop (Board board, Color color)
  {
    super (board, color);
  }

   @Override public boolean[][] possibleMoves ()
  {

    boolean[][]mat =
      new boolean[getBoard ().getRows ()][getBoard ().getColumns ()];

    Position p = new Position (0, 0);

    // northwest
    p.setValues (position.getRow () - 1, position.getColumn () - 1);
    while (getBoard ().positionExists (p) && !getBoard ().thereIsAPiece (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
	p.setValues (p.getRow () - 1, p.getColumn () - 1);
      }
    if (getBoard ().positionExists (p) && isThereOpponentPiece (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
      }

    // northeast
    p.setValues (position.getRow () - 1, position.getColumn () + 1);
    while (getBoard ().positionExists (p) && !getBoard ().thereIsAPiece (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
	p.setValues (p.getRow () - 1, p.getColumn () + 1);
      }
    if (getBoard ().positionExists (p) && isThereOpponentPiece (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
      }

    // southeast
    p.setValues (position.getRow () + 1, position.getColumn () + 1);
    while (getBoard ().positionExists (p) && !getBoard ().thereIsAPiece (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
	p.setValues (p.getRow () + 1, p.getColumn () + 1);
      }
    if (getBoard ().positionExists (p) && isThereOpponentPiece (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
      }

    // below
    p.setValues (position.getRow () + 1, position.getColumn () - 1);
    while (getBoard ().positionExists (p) && !getBoard ().thereIsAPiece (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
	p.setValues (p.getRow () + 1, p.getColumn () - 1);
      }
    if (getBoard ().positionExists (p) && isThereOpponentPiece (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
      }

    return mat;
  }

  @Override public String toString ()
  {
    return "B";
  }
}

class King extends ChessPiece
{

  private ChessMatch chessMatch;

  public King (Board board, Color color, ChessMatch chessMatch)
  {
    super (board, color);
    this.chessMatch = chessMatch;
  }
  public ChessMatch getChessMatch ()
  {
    return chessMatch;
  }
  @Override public String toString ()
  {
    return "R";
  }

  private boolean canMove (Position position)
  {
    ChessPiece p = (ChessPiece) getBoard ().piece (position);
    return p == null || p.getColor () != getColor ();
  }

  private boolean testRookCastling (Position position)
  {
    ChessPiece p = (ChessPiece) getBoard ().piece (position);
    return position != null && p instanceof Rook && getColor () == getColor ()
      && p.getMoveCount () == 0;
  }

  @Override public boolean[][]possibleMoves ()
  {
    boolean[][]mat =
      new boolean[getBoard ().getRows ()][getBoard ().getColumns ()];
    Position p = new Position (0, 0);

    //above
    p.setValues (position.getRow () - 1, position.getColumn ());
    if (getBoard ().positionExists (p) && canMove (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
      }
    //below
    p.setValues (position.getRow () + 1, position.getColumn ());
    if (getBoard ().positionExists (p) && canMove (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
      }
    //left
    p.setValues (position.getRow (), position.getColumn () - 1);
    if (getBoard ().positionExists (p) && canMove (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
      }

    //right
    p.setValues (position.getRow (), position.getColumn () + 1);
    if (getBoard ().positionExists (p) && canMove (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
      }


    p.setValues (position.getRow () - 1, position.getColumn () - 1);
    if (getBoard ().positionExists (p) && canMove (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
      }


    p.setValues (position.getRow () + 1, position.getColumn () + 1);
    if (getBoard ().positionExists (p) && canMove (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
      }


    p.setValues (position.getRow () + 1, position.getColumn () - 1);
    if (getBoard ().positionExists (p) && canMove (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
      }


    //southeast
    p.setValues (position.getRow () - 1, position.getColumn () + 1);
    if (getBoard ().positionExists (p) && canMove (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
      }


    //Special move castling
    if (getMoveCount () == 0 && !chessMatch.isCheck ())
      {
	//# special move castling kingside rook
	Position positionT1 =
	  new Position (position.getRow (), position.getColumn () + 3);
	if (testRookCastling (positionT1))
	  {
	    Position p1 =
	      new Position (position.getRow (), position.getColumn () + 1);
	    Position p2 =
	      new Position (position.getRow (), position.getColumn () + 2);
	    if (getBoard ().piece (p1) == null
		&& getBoard ().piece (p2) == null)
	      {
		mat[position.getRow ()][position.getColumn () + 2] = true;
	      }
	  }
	//# special move castling queenside rook
	Position positionT2 =
	  new Position (position.getRow (), position.getColumn () - 4);
	if (testRookCastling (positionT2))
	  {
	    Position p1 =
	      new Position (position.getRow (), position.getColumn () - 1);
	    Position p2 =
	      new Position (position.getRow (), position.getColumn () - 2);
	    Position p3 =
	      new Position (position.getRow (), position.getColumn () - 3);
	    if (getBoard ().piece (p1) == null
		&& getBoard ().piece (p2) == null
		&& getBoard ().piece (p3) == null)
	      {
		mat[position.getRow ()][position.getColumn () - 2] = true;
	      }
	  }

      }
    return mat;
  }


}


class Knight extends ChessPiece
{
  public Knight (Board board, Color color)
  {
    super (board, color);
  }

  public String toString ()
  {
    return "C";
  }

  private boolean canMove (Position position)
  {
    ChessPiece p = (ChessPiece) getBoard ().piece (position);
    return p == null || p.getColor () != getColor ();
  }

  @Override public boolean[][]possibleMoves ()
  {
    boolean[][]mat =
      new boolean[getBoard ().getRows ()][getBoard ().getColumns ()];
    Position p = new Position (0, 0);

    p.setValues (position.getRow () - 1, position.getColumn () - 2);
    if (getBoard ().positionExists (p) && canMove (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
      }

    p.setValues (position.getRow () - 2, position.getColumn () - 1);
    if (getBoard ().positionExists (p) && canMove (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
      }

    p.setValues (position.getRow () - 2, position.getColumn () + 1);
    if (getBoard ().positionExists (p) && canMove (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
      }

    p.setValues (position.getRow () - 1, position.getColumn () + 2);
    if (getBoard ().positionExists (p) && canMove (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
      }

    p.setValues (position.getRow () + 1, position.getColumn () + 2);
    if (getBoard ().positionExists (p) && canMove (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
      }

    p.setValues (position.getRow () + 2, position.getColumn () + 1);
    if (getBoard ().positionExists (p) && canMove (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
      }

    p.setValues (position.getRow () + 2, position.getColumn () - 1);
    if (getBoard ().positionExists (p) && canMove (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
      }

    p.setValues (position.getRow () + 1, position.getColumn () - 2);
    if (getBoard ().positionExists (p) && canMove (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
      }


    return mat;
  }


}

class Pawn extends ChessPiece
{

  private ChessMatch chessMatch;

  public Pawn (Board board, Color color, ChessMatch chessMatch)
  {
    super (board, color);
    this.chessMatch = chessMatch;
  }

   @Override public boolean[][] possibleMoves ()
  {
    boolean[][]mat =
      new boolean[getBoard ().getRows ()][getBoard ().getColumns ()];
    Position p = new Position (0, 0);

    if (getColor () == Color.WHITE)
      {
	p.setValues (position.getRow () - 1, position.getColumn ());
	if (getBoard ().positionExists (p) && !getBoard ().thereIsAPiece (p))
	  {
	    mat[p.getRow ()][p.getColumn ()] = true;
	  }
	p.setValues (position.getRow () - 2, position.getColumn ());
	Position p2 =
	  new Position (position.getRow () - 1, position.getColumn ());
	if (getBoard ().positionExists (p) && !getBoard ().thereIsAPiece (p)
	    && getBoard ().positionExists (p2)
	    && !getBoard ().thereIsAPiece (p2) && getMoveCount () == 0)
	  {
	    mat[p.getRow ()][p.getColumn ()] = true;
	  }
	p.setValues (position.getRow () - 1, position.getColumn () - 1);
	if (getBoard ().positionExists (p) && isThereOpponentPiece (p))
	  {
	    mat[p.getRow ()][p.getColumn ()] = true;
	  }
	p.setValues (position.getRow () - 1, position.getColumn () + 1);
	if (getBoard ().positionExists (p) && isThereOpponentPiece (p))
	  {
	    mat[p.getRow ()][p.getColumn ()] = true;
	  }
	// #specialmove en passant white
	if (position.getRow () == 3)
	  {
	    Position left =
	      new Position (position.getRow (), position.getColumn () - 1);
	    if (getBoard ().positionExists (left)
		&& isThereOpponentPiece (left)
		&& getBoard ().piece (left) ==
		chessMatch.getEnPassantVulnerable ())
	      {
		mat[left.getRow () - 1][left.getColumn ()] = true;
	      }
	    Position right =
	      new Position (position.getRow (), position.getColumn () + 1);
	    if (getBoard ().positionExists (right)
		&& isThereOpponentPiece (right)
		&& getBoard ().piece (right) ==
		chessMatch.getEnPassantVulnerable ())
	      {
		mat[right.getRow () - 1][right.getColumn ()] = true;
	      }
	  }
      }
    else
      {
	p.setValues (position.getRow () + 1, position.getColumn ());
	if (getBoard ().positionExists (p) && !getBoard ().thereIsAPiece (p))
	  {
	    mat[p.getRow ()][p.getColumn ()] = true;
	  }
	p.setValues (position.getRow () + 2, position.getColumn ());
	Position p2 =
	  new Position (position.getRow () + 1, position.getColumn ());
	if (getBoard ().positionExists (p) && !getBoard ().thereIsAPiece (p)
	    && getBoard ().positionExists (p2)
	    && !getBoard ().thereIsAPiece (p2) && getMoveCount () == 0)
	  {
	    mat[p.getRow ()][p.getColumn ()] = true;
	  }
	p.setValues (position.getRow () + 1, position.getColumn () + 1);
	if (getBoard ().positionExists (p) && isThereOpponentPiece (p))
	  {
	    mat[p.getRow ()][p.getColumn ()] = true;
	  }
	p.setValues (position.getRow () + 1, position.getColumn () - 1);
	if (getBoard ().positionExists (p) && isThereOpponentPiece (p))
	  {
	    mat[p.getRow ()][p.getColumn ()] = true;
	  }
	// #specialmove en passant black
	if (position.getRow () == 4)
	  {
	    Position left =
	      new Position (position.getRow (), position.getColumn () - 1);
	    if (getBoard ().positionExists (left)
		&& isThereOpponentPiece (left)
		&& getBoard ().piece (left) ==
		chessMatch.getEnPassantVulnerable ())
	      {
		mat[left.getRow () + 1][left.getColumn ()] = true;
	      }
	    Position right =
	      new Position (position.getRow (), position.getColumn () + 1);
	    if (getBoard ().positionExists (right)
		&& isThereOpponentPiece (right)
		&& getBoard ().piece (right) ==
		chessMatch.getEnPassantVulnerable ())
	      {
		mat[right.getRow () + 1][right.getColumn ()] = true;
	      }
	  }
      }

    return mat;
  }

  @Override public String toString ()
  {
    return "P";
  }
}

class Rook extends ChessPiece
{


  public Rook (Board board, Color color)
  {
    super (board, color);
  }

   @Override public String toString ()
  {
    return "T";
  }


  @Override public boolean[][]possibleMoves ()
  {
    boolean[][]mat =
      new boolean[getBoard ().getRows ()][getBoard ().getColumns ()];

    Position p = new Position (0, 0);

    // above
    p.setValues (position.getRow () - 1, position.getColumn ());
    while (getBoard ().positionExists (p) && !getBoard ().thereIsAPiece (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
	p.setRow (p.getRow () - 1);
      }
    if (getBoard ().positionExists (p) && isThereOpponentPiece (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
      }

    // left
    p.setValues (position.getRow (), position.getColumn () - 1);
    while (getBoard ().positionExists (p) && !getBoard ().thereIsAPiece (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
	p.setColumn (p.getColumn () - 1);
      }
    if (getBoard ().positionExists (p) && isThereOpponentPiece (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
      }

    // right
    p.setValues (position.getRow (), position.getColumn () + 1);
    while (getBoard ().positionExists (p) && !getBoard ().thereIsAPiece (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
	p.setColumn (p.getColumn () + 1);
      }
    if (getBoard ().positionExists (p) && isThereOpponentPiece (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
      }

    // below
    p.setValues (position.getRow () + 1, position.getColumn ());
    while (getBoard ().positionExists (p) && !getBoard ().thereIsAPiece (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
	p.setRow (p.getRow () + 1);
      }
    if (getBoard ().positionExists (p) && isThereOpponentPiece (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
      }

    return mat;
  }
}

class Queen extends ChessPiece
{


  public Queen (Board board, Color color)
  {
    super (board, color);
  }

  public String toString ()
  {
    return "A";
  }

  @Override public boolean[][]possibleMoves ()
  {
    boolean[][]mat =
      new boolean[getBoard ().getRows ()][getBoard ().getColumns ()];

    Position p = new Position (0, 0);

    // above
    p.setValues (position.getRow () - 1, position.getColumn ());
    while (getBoard ().positionExists (p) && !getBoard ().thereIsAPiece (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
	p.setRow (p.getRow () - 1);
      }
    if (getBoard ().positionExists (p) && isThereOpponentPiece (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
      }

    // left
    p.setValues (position.getRow (), position.getColumn () - 1);
    while (getBoard ().positionExists (p) && !getBoard ().thereIsAPiece (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
	p.setColumn (p.getColumn () - 1);
      }
    if (getBoard ().positionExists (p) && isThereOpponentPiece (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
      }

    // right
    p.setValues (position.getRow (), position.getColumn () + 1);
    while (getBoard ().positionExists (p) && !getBoard ().thereIsAPiece (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
	p.setColumn (p.getColumn () + 1);
      }
    if (getBoard ().positionExists (p) && isThereOpponentPiece (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
      }

    // below
    p.setValues (position.getRow () + 1, position.getColumn ());
    while (getBoard ().positionExists (p) && !getBoard ().thereIsAPiece (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
	p.setRow (p.getRow () + 1);
      }
    if (getBoard ().positionExists (p) && isThereOpponentPiece (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
      }
    // northwest
    p.setValues (position.getRow () - 1, position.getColumn () - 1);
    while (getBoard ().positionExists (p) && !getBoard ().thereIsAPiece (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
	p.setValues (p.getRow () - 1, p.getColumn () - 1);
      }
    if (getBoard ().positionExists (p) && isThereOpponentPiece (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
      }

    // northeast
    p.setValues (position.getRow () - 1, position.getColumn () + 1);
    while (getBoard ().positionExists (p) && !getBoard ().thereIsAPiece (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
	p.setValues (p.getRow () - 1, p.getColumn () + 1);
      }
    if (getBoard ().positionExists (p) && isThereOpponentPiece (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
      }

    // southeast
    p.setValues (position.getRow () + 1, position.getColumn () + 1);
    while (getBoard ().positionExists (p) && !getBoard ().thereIsAPiece (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
	p.setValues (p.getRow () + 1, p.getColumn () + 1);
      }
    if (getBoard ().positionExists (p) && isThereOpponentPiece (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
      }

    // below
    p.setValues (position.getRow () + 1, position.getColumn () - 1);
    while (getBoard ().positionExists (p) && !getBoard ().thereIsAPiece (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
	p.setValues (p.getRow () + 1, p.getColumn () - 1);
      }
    if (getBoard ().positionExists (p) && isThereOpponentPiece (p))
      {
	mat[p.getRow ()][p.getColumn ()] = true;
      }

    return mat;
  }
}

class BoardColors
{
  public static final String ANSI_RESET = "\u001B[0m";
  public static final String ANSI_BLACK = "\u001B[30m";
  public static final String ANSI_RED = "\u001B[31m";
  public static final String ANSI_GREEN = "\u001B[32m";
  public static final String ANSI_YELLOW = "\u001B[33m";
  public static final String ANSI_BLUE = "\u001B[34m";
  public static final String ANSI_PURPLE = "\u001B[35m";
  public static final String ANSI_CYAN = "\u001B[36m";
  public static final String ANSI_WHITE = "\u001B[37m";

  public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
  public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
  public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
  public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
  public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
  public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
  public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
  public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";
}



class BoardView
{

  public static void clearScreen ()
  {
    System.out.print ("\033[H\033[2J");
    System.out.flush ();
  }

  public static ChessPosition readChessPosition (Scanner sc)
  {
    try
    {
      String s = sc.nextLine ();
      char column = s.charAt (0);
      int row = Integer.parseInt (s.substring (1));
      return new ChessPosition (column, row);
    }
    catch (RuntimeException e)
    {
      throw new
	InputMismatchException
	("Error reading the position on the board. Valid values between a1 to h8");
    }
  }
  public static void printMatch (ChessMatch chessMatch,
				 List < ChessPiece > capturedPieces)
  {
    printBoard (chessMatch.getPieces ());
    System.out.println ();
    capturedPieces (capturedPieces);
    System.out.println ("Shift: " + chessMatch.getTurn ());
    if (!chessMatch.isCheckMate ())
      {
	System.out.println ("waiting for the player: " +
			    chessMatch.getCurrentPlayer ());
	if (chessMatch.isCheck ())
	  {
	    System.out.println ("Check!");
	  }
      }
    else
      {
	System.out.println ("CHECKMATE!");
	System.out.println ("Winner: " + chessMatch.getCurrentPlayer ());
      }

  }

  public static void printBoard (ChessPiece[][]pieces)
  {
    for (int i = 0; i < pieces.length; i++)
      {
	System.out.print ((8 - i) + " ");
	for (int j = 0; j < pieces.length; j++)
	  {
	    printPiece (pieces[i][j], false);
	  }
	System.out.println ();
      }
    System.out.println ("  a b c d e f g h");
  }

  public static void printBoard (ChessPiece[][]pieces,
				 boolean[][]possibleMoves)
  {
    for (int i = 0; i < pieces.length; i++)
      {
	System.out.print ((8 - i) + " ");
	for (int j = 0; j < pieces.length; j++)
	  {
	    printPiece (pieces[i][j], possibleMoves[i][j]);
	  }
	System.out.println ();
      }
    System.out.println ("  a b c d e f g h");
  }

  private static void printPiece (ChessPiece piece, boolean background)
  {
    if (background)
      {
	System.out.print (BoardColors.ANSI_BLUE_BACKGROUND);
      }
    if (piece == null)
      {
	System.out.print ("-" + BoardColors.ANSI_RESET);
      }
    else
      {
	if (piece.getColor () == Color.WHITE)
	  {
	    System.out.print (BoardColors.ANSI_WHITE + piece +
			      BoardColors.ANSI_RESET);
	  }
	else
	  {
	    System.out.print (BoardColors.ANSI_YELLOW + piece +
			      BoardColors.ANSI_RESET);
	  }
      }
    System.out.print (" ");
  }
  private static void capturedPieces (List < ChessPiece > chessPieces)
  {
    List < ChessPiece > white =
      chessPieces.stream ().filter (x->x.getColor () ==
				    Color.WHITE).collect (Collectors.
							  toList ());

    List < ChessPiece > black =
      chessPieces.stream ().filter (x->x.getColor () ==
				    Color.BLACK).collect (Collectors.
							  toList ());
    System.out.println ("captured pieces: ");
    System.out.print ("white pieces: ");
    System.out.print (BoardColors.ANSI_WHITE);
    System.out.println (Arrays.toString (white.toArray ()));
    System.out.print (BoardColors.ANSI_RESET);
    System.out.print ("black pieces: ");
    System.out.print (BoardColors.ANSI_YELLOW);
    System.out.println (Arrays.toString (black.toArray ()));


  }
}


public class Main
{
  public static void main (String[]args)
  {
    // write your code here
    Scanner sc = new Scanner (System.in);
    ChessMatch chessMatch = new ChessMatch ();
      List < ChessPiece > captureChessPieces = new ArrayList <> ();
      System.out.print (BoardColors.ANSI_YELLOW_BACKGROUND);
      System.out.print (BoardColors.ANSI_BLACK);
      System.out.println ("WELCOME TO CHESS GAME FOR CONSOLE\n\n");
      System.out.print ("To start the game Type y or yes :");
    String init = sc.nextLine ();
    if (init.equals ("Y") || init.equals ("y") || init.equals ("yes")
	|| init.equals ("YES"))
      {
	while (!chessMatch.isCheckMate ())
	  {
	    try
	    {
	      System.out.print (BoardColors.ANSI_RESET);
	      BoardView.clearScreen ();
	      BoardView.printMatch (chessMatch, captureChessPieces);
	      System.out.println ();
	      System.out.print ("home position: ");
	      ChessPosition source = BoardView.readChessPosition (sc);

	        boolean[][] possibleMoves = chessMatch.possibleMoves (source);
	        BoardView.clearScreen ();
	        BoardView.printBoard (chessMatch.getPieces (), possibleMoves);
	        System.out.println ();
	        System.out.print ("target position: ");
	      ChessPosition target = BoardView.readChessPosition (sc);
	      ChessPiece capturedPiece =
		chessMatch.performChessMove (source, target);
	      if (capturedPiece != null)
		{
		  captureChessPieces.add (capturedPiece);
		}
	      if (chessMatch.getPromoted () != null)
		{
		  System.out.
		    println
		    ("Enter the letter of the part to be chosen: (A/C/T/B)");
		  String type = sc.nextLine ();
		  chessMatch.replacepromotedPiece (type);
		}
	    }
	    catch (ChessException | InputMismatchException e)
	    {
	      System.out.println (e.getMessage ());
	      sc.nextLine ();
	    }
	  }

	BoardView.clearScreen ();
	BoardView.printMatch (chessMatch, captureChessPieces);

      }
    else
      {
	System.out.close ();
      }

  }
}
