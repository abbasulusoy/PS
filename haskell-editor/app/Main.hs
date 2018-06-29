module Main where

import Lib
import System.Console.ANSI

main :: IO ()
main = do
    clearScreen
    setTitle "Haskell-Editor"
    putStrLn "Write something:"
    line <- getLine
    putStrLn line

    
