module Editor where

import Control.Exception
import Control.Monad.State
import Data.List
import Data.Char (ord)
-- import Data.List.Split
import System.Console.ANSI  -- http://hackage.haskell.org/package/ansi-terminal-0.5.0/docs/System-Console-ANSI.html#2
import System.IO
import System.IO.Error

import PrittyPrint

-- import LanPrettyprint

-- ------------------------------------------------<-----------------------------
-- Constants
-- -----------------------------------------------------------------------------
type Text = [Char]
type EditorState = (Int, Text) -- (Cursor pos, text)

-- initial state
initialState = (0,[])

-- Size of the xterm window our editor runs in. Assumed to be constant (i.e., don't change the window size of the xterm or things will get really weird)
nrCols = 132
nrRows = 43 - 4
-- footer text (make sure it's shorter than nrCols)
footer = "[ESC+o] Open file\t" ++  -- Note: We do not use CTRL+, as those key combinations are appearently caught by the terminal
        "[ESC+s] Save file\t" ++
        "[ESC+n] New file\t" ++
        "[CTRL+c] Exit\t"

ofPrompt = "Tell me which file you want to edit: "
sfPrompt = "Tell me where to save: "
whoops = "Whoops, something went wrong: "
nf = "File"
-- -----------------------------------------------------------------------------
-- main
-- -----------------------------------------------------------------------------
editor :: IO ()
editor = do
    -- configure streams
    hSetBuffering stdin NoBuffering
    hSetBuffering stdout NoBuffering
    hSetEcho stdin False
    clearScreen
    printFooter 0 0
    setCursorPosition 0 0
    setTitle nf
    execStateT process initialState
    clearScreen
    putStrLn "bye" -- this is only here because the last statement needs to be an expression with result type IO

-- -----------------------------------------------------------------------------
-- Handling the editors state
-- -----------------------------------------------------------------------------
process :: StateT EditorState IO ()
process = do
    (lift readNext) >>= processInput        -- wait for next keystroke and process it
    (p,text) <- get                         -- get current state of our editor
    lift clearScreen
    let cp = myCursorPosition text p in       -- calculate cursor position
        do
            lift $ setCursorPosition 0 0            -- set cursor to 0,0 so that...
            lift $ putStr (textToShow (fst cp) text)         -- ...text is printed correctly
            lift $ printFooter (fst cp) (snd cp)
            lift $ setCursorPosition (mod (fst cp) nrRows) (snd cp)  -- set actual cursor position
    process

-- -----------------------------------------------------------------------------
-- handling key strokes
-- -----------------------------------------------------------------------------
insertChar :: Char -> StateT EditorState IO ()
insertChar c = state $ \(p,xs) -> ((), (p+1 , insertAt c xs p))

insertAt :: Char -> Text -> Int -> Text
insertAt c xs p = let (ys,zs) = splitAt p xs in ys++c:zs

backspace ::StateT EditorState IO ()
backspace = state $ \(p,xs) -> ((), (max 0 (p-1), removeAt (p-1) xs))

deleteKey ::StateT EditorState IO ()
deleteKey = state $ \(p,xs) -> ((), (p, removeAt (p) xs))

removeAt :: Int -> Text -> Text
removeAt (-1) xs = xs
removeAt i xs | i >= (length xs) = xs
              | otherwise = let (ys,zs) = splitAt i xs in ys++(tail zs)

tabKey :: StateT EditorState IO ()
tabKey = state $ \(p,xs) -> ((), (p+4 , let (ys,zs) = splitAt p xs in ys++"    "++zs))
-- -----------------------------------------------------------------------------
-- Changing cursor position
-- -----------------------------------------------------------------------------
arrowRight :: StateT EditorState IO ()
arrowRight= state $ \(p,xs) -> ((), (min (p+1) (length xs), xs))

arrowLeft :: StateT EditorState IO ()
arrowLeft = state $ \(p,xs) -> ((), (max (p-1) 0, xs))

arrowUp :: StateT EditorState IO ()
arrowUp = state $ \(p,xs) -> ((), (calcUp xs p (myCursorPosition xs p), xs))

calcUp :: Text -> Int -> (Int, Int) -> Int
calcUp xs p (0,c) = c
calcUp xs p (r,c)
    | c > nrCols = p - nrCols
    | otherwise = sumLinesTo xs (r-1) + min c (length ((lines xs)!!(r-1)))

sumLinesTo :: Text -> Int -> Int
sumLinesTo xs i
    | i <= 0 = 0
    | otherwise = sum $ map ((+1) . length) (take i $ lines xs)

arrowDown :: StateT EditorState IO ()
arrowDown = state $ \(p,xs) -> ((), (calcDown xs p (myCursorPosition xs p), xs))

calcDown :: Text -> Int -> (Int, Int) -> Int
calcDown xs p (r,c)
    | r < length (lines xs) && length ((lines xs)!!r) >= c + nrCols = p + nrCols
    | r + 1 >= length (lines xs) = p  -- TODO: there is a small bug here. If last line contains only '\n' we can never reach it with the arrow down key
    | otherwise = sumLinesTo xs (r+1) + min c (length ((lines xs)!!(r+1)))
-- -----------------------------------------------------------------------------
-- Calculating cursor position on screen
-- Arguments:   String text: contains the text to display, including \n;
--              Int p: cursor position within that text
-- Return:      Int cp=(x,y): cursor position on screen
-- Algorithm:
--      IF cursor is at very end of text AND last character is \n
--          THEN RETURN (nr of lines + nr wrapped lines, 0)
--          ELSE    l1 <- Split the text into lines
--                  l2 <- length of each line + 1 (we assume that each line ends with \n)
--                  l3 <- prefix sum over l2
--                  line <- s.t. l3[line] > p (find the line in which text[p] will be displayed)
--                  line <- line + nr lines with length > nrCols up until position p in the text
--                  column <- p - nr of characters in all lines before line, mod nr of columns
--                  RETURN (line, column)
-- -----------------------------------------------------------------------------
myCursorPosition :: Text -> Int -> (Int, Int)
myCursorPosition [] _ = (0,0)
myCursorPosition text p =
    if (p == (length text)) && (text!!(p-1) == '\n')
       then (length (lines text) + (nrLineWraps text p) , 0) -- If the last character is a newline, the else branch will give a wrong line number (because we add +1 to the length of every line)
       else (lineNr + (nrLineWraps text p),  mod colNr nrCols )
            where   sl = scanl (+) 0 $ map ((+1) . length) (lines text) -- find out how many characters there are in each line (+1 for newlines) and do prefix sum
                    lineNr = case findIndex (>p) sl of  -- find out in which line we are at
                        Just val -> val - 1
                        Nothing -> -1          --TODO: this can never happen
                    colNr = mod (p - sl!!(lineNr)) nrCols  -- substract nr of characters in all lines before lineNr and do mod nrCols since we wrap long lines)

-- Count number of line wraps that occur in the text up until the given position
-- In other words: find lines longer than nrCols (up to given position p in text) and then calculate how often they will wrap
nrLineWraps :: Text -> Int -> Int
nrLineWraps text p = sum wrapCnt
    where
        t = lines (take p text)                 -- lines up until position p
        ll = filter ((>=nrCols) . length) t     -- long lines
        lll = map length ll                     -- length of the long lines
        wrapCnt = map (\x -> div x nrCols) lll           -- how often they will wrap

-- -----------------------------------------------------------------------------
-- Syntax highlighting
-- -----------------------------------------------------------------------------
highlight:: String -> String
highlight s = pretty s  -- TODO: This is a dummy implementation. Highlight portions of text by inserting color codes like this:
-- "\x1b[31m" ++ "Make me red" ++ "\x1b[0m" ++ " but leave me black" --color text
-- Here be syntax highlighting magic

textToShow:: Int -> String -> String
textToShow c xs = unlines $ take nrRows xs1
    where
        xs1 = drop (nrRows * (div c nrRows)) xsp
        xsp = lines $ highlight xs
-- -----------------------------------------------------------------------------
-- Input processing
-- -----------------------------------------------------------------------------
-- read the next character from keyboard
readNext :: IO Char
readNext = getChar

processInput :: Char -> StateT EditorState IO ()
processInput '\x1b' = (lift readNext) >>= esc  -- matched escape character; read (next part of) escape character code and process it
processInput '\x7f' =  backspace -- backspace
processInput '\x7e' = deleteKey -- delete
processInput '\x09' = tabKey -- tab
processInput c = insertChar c  -- any other character is simply added to our text buffer

-- Assumes the character read before c was ESC and thus processes the following characters as escape codes.
esc :: Char -> StateT EditorState IO ()
esc '[' = (lift readNext) >>= escSqBracket
esc 'o' = (lift $ savelyOpen ofPrompt) >>= loadText --in put (0, loadedText)
esc 's' = get >>= saveText
esc 'n' = do
    put initialState
    lift $ setTitle nf
esc c = return () -- ignore

-- Assumes that the characters read before c were ESC[ and processes the following character as part of the arrow key scancode.
escSqBracket :: Char -> StateT EditorState IO ()
escSqBracket 'A' = arrowUp
escSqBracket 'B' = arrowDown
escSqBracket 'C' = arrowRight
escSqBracket 'D' = arrowLeft
escSqBracket c = return () -- ignore

loadText :: Text -> StateT EditorState IO ()
loadText t = put (0, filter (/= '\r') t)

saveText :: (Int, Text) ->  StateT EditorState IO ()
saveText (p,t) = lift $ savelyWrite t sfPrompt
-- -----------------------------------------------------------------------------
-- Modes:
-- Edit mode: catch all keystrokes and process them
-- Default mode: Let the terminal handle keystrokes (i.e., print the in the terminal)
-- -----------------------------------------------------------------------------
echoOff = hSetEcho stdin False
echoOn = hSetEcho stdin True


-- -----------------------------------------------------------------------------
-- Screen manipulation
-- -----------------------------------------------------------------------------
-- Print the footer
printFooter :: Int -> Int -> IO ()
printFooter r c = do
    setCursorPosition (nrRows + 1) 0
    putStrLn $ replicate nrCols '='
    putStr ("  " ++ show r ++ ":" ++ show c ++ "\t\t")
    putStrLn footer
    putStr $ replicate nrCols '='

statusLine :: String -> IO ()
statusLine s = do
    echoOn
    setCursorPosition nrRows 0
    putStr s
    setCursorPosition nrRows (length s)

-- -----------------------------------------------------------------------------
-- File IO
-- -----------------------------------------------------------------------------
savelyOpen :: String -> IO String
savelyOpen s = do
    statusLine s
    rf <- getLine
    result <- try (readFile rf) :: IO (Either SomeException String)
    case result of
        Left ex -> savelyOpen $ whoops ++ show ex ++ " " ++ ofPrompt
        Right t -> do
            setTitle rf
            echoOff
            return t

savelyWrite:: Text -> String -> IO ()
savelyWrite t s = do
    statusLine s
    wf <- getLine
    result <- try (writeFile wf t) :: IO (Either SomeException ())
    case result of
        Left ex -> savelyWrite t (whoops ++ show ex ++ " " ++ sfPrompt)
        Right res -> do
            setTitle wf
            echoOff


-- putStrLn $ "\x1b[31m" ++ "Make me red" ++ "\x1b[0m" ++ " but leave me black" --color text
