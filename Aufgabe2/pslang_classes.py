class Rule:
    def __init__(self, name, body):
        self.name = name
        self.body = body

class Body:
    def __init__(self, params, instructions, ret):
        self.params = params
        self.instructions = instructions
        self.ret = ret

class Instruction:
    def __init__(self, is_shell, name, params, ret):
        self.is_shell = is_shell
        self.name = name
        self.params = params
        self.ret = ret

class Parser:
    def __init__(self):
        pass

    def parseRules(self, str_rules):
        # TODO: read string and return list of Rules
        pass

    def getBody(self, rule):
        # TODO: read Rule and return Body of Rule
        pass

    def getParams(self, body):
        # TODO: read Body and return list of params (strings)
        pass

    def getInstructionUnparsed(self, body):
        # TODO: read Body and return list of instructions (raw strings, UNPARSED)
        pass

    def getReturn(self, body):
        # TODO: read Body and return Body's return value as string
        pass

    def parseInstructions(self, str_instructions):
        # TODO: read string and return list of Instructions
        pass

class Executor:
    def __init__(self):
        pass
