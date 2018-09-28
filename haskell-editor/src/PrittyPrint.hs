module PrittyPrint where

import Data.Char

normal = "\x1b[0m"
red = "\x1b[31m"
green = "\x1b[32m"
blue = "\x1b[34m"
yellow = "\x1b[33m"
magenta = "\x1b[35m"
cyan = "\x1b[36m"


pretty:: String -> String
pretty s = snd (prettyColor normal (prettyProg (s, [])))

prettyProg:: (String, String) -> (String, String)
prettyProg ([], w) = ([], w)
prettyProg (r, w) = prettyProg (prettyRule (r, w))

prettyRule:: (String, String) -> (String, String)
prettyRule ([], w) = ([], w)
prettyRule (r, w) = prettyBody (prettyHead (r, w))

prettyHead:: (String, String) -> (String, String)
prettyHead ([], w) = ([], w)
prettyHead (r, w) = prettyChar ':' (prettySkip (prettyName (r, w)))

prettyName:: (String, String) -> (String, String)
prettyName ([], w) = ([], w)
prettyName (r, w) = prettyColor normal (prettyToken (prettyOptChar '$' (prettyColor green (prettySkip (r, w)))))

prettyBody:: (String, String) -> (String, String)
prettyBody ([], w) = ([], w)
prettyBody (r, w) = prettyChar '.' (prettySkip (prettyPattern (prettyInstruction (prettyParam (r, w)))))

prettyParam:: (String, String) -> (String, String)
prettyParam ([], w) = ([], w)
prettyParam (r, w) = prettyBracked magenta prettyParamImpl (r, w)

prettyParamImpl:: (String, String) -> (String, String)
prettyParamImpl (r, w) = prettyIfChar ',' prettyParamImpl (prettySkip (prettyPatternImpl (prettySkip(r, w))))

prettyPattern:: (String, String) -> (String, String)
prettyPattern ([], w) = ([], w)
prettyPattern (r, w) = prettyBracked cyan prettyPatternImpl (r, w)

prettyPatternImpl:: (String, String) -> (String, String)
prettyPatternImpl (r, w) = prettyToken (prettyOptChar '*' (prettyToken (prettyOptChar '+' (prettyOptChar '*' (r, w)))))

prettyInstruction:: (String, String) -> (String, String)
prettyInstruction ([], w) = ([], w)
prettyInstruction (r, w) = prettyBracked yellow prettyInstructionImpl (r, w)

prettyInstructionImpl:: (String, String) -> (String, String)
prettyInstructionImpl ([], w) = ([], w)
prettyInstructionImpl (c:r, w)
  | c == ')' = (c:r, w)
  | otherwise = prettyInstructionImpl (prettySkip (prettyOptChar ';' (prettySkip (prettyPattern (prettyParam (prettyName (prettySkip (c:r, w))))))))


prettyBracked:: String -> ((String, String) -> (String, String)) -> (String, String) -> (String, String)
prettyBracked _ _ ([], w) = ([], w)
prettyBracked color prettyFunc (r, w) = prettyColorChar ')' color (prettySkip (prettyFunc (prettySkip (prettyColorChar '(' color (prettySkip (r, w))))))

prettyToken:: (String, String) -> (String, String)
prettyToken ([], w) = ([], w)
prettyToken (c:r, w)
  | isAlphaNum c = prettyToken (r, w ++ [c])
  | isFree c = (c:r, w)
  | otherwise = (c:r, w)

prettyChar:: Char -> (String, String) -> (String, String)
prettyChar _ ([], w) = ([], w)
prettyChar ref (c:r, w)
  | ref == c = (r, w ++ [c])
  | otherwise = prettyError (c:r, w)

prettyOptChar:: Char -> (String, String) -> (String, String)
prettyOptChar _ ([], w) = ([], w)
prettyOptChar ref (c:r, w)
    | ref == c = (r, w ++ [c])
    | otherwise = (c:r, w)

prettyIfChar:: Char -> ((String, String) -> (String, String)) -> (String, String) -> (String, String)
prettyIfChar _ _ ([], w) = ([], w)
prettyIfChar ref func (c:r, w)
    | ref == c =  func (r, w ++ [c])
    | otherwise = (c:r, w)

prettyColorChar:: Char -> String -> (String, String) -> (String, String)
prettyColorChar _ _ ([], w) = ([], w)
prettyColorChar ref color (c:r, w)
  | ref == c = (r, w ++ color ++ [c] ++ normal)
  | otherwise = prettyError (c:r, w)

prettySkip:: (String, String) -> (String, String)
prettySkip (' ':r, w) = prettySkip (r, w ++ " ")
prettySkip ('\n':r, w) = prettySkip (r, w ++ "\n")
prettySkip (r, w) = (r, w)

prettyError:: (String, String) -> (String, String)
prettyError (r, w) = ([], w ++ red ++ r)

prettyColor:: String -> (String, String) -> (String, String)
prettyColor color (r, w) = (r, w ++ normal ++ color)

isFree:: Char -> Bool
isFree c
  | c == '\n' || c == ' ' = True -- Todo
  | otherwise = False
