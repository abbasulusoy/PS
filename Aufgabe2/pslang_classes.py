import subprocess, uuid


class Rule:
    def __init__(self, is_shell, name, params, instructions, ret):
        self.rule_id = str(uuid.uuid1())
        self.is_shell = is_shell
        self.name = name
        self.body = Body(params, instructions, ret)


class Instruction:
    def __init__(self, is_shell, name, params, ret):
        self.instr_id = str(uuid.uuid1())
        self.is_shell = is_shell
        self.name = name
        self.body = Body(params, None, ret)


class Body:
    def __init__(self, params, instructions, ret):
        self.params = params
        self.instructions = instructions
        self.ret = ret


class Variable:
    def __init__(self, name, vtype, value):
        self.var_id = str(uuid.uuid1())
        self.name = name
        self.vtype = vtype
        self.value = value

    @staticmethod
    def copyOf(var):
        tmp = Variable(var.name, var.vtype, var.value)
        tmp.var_id = var.var_id
        return tmp


class Parser:
    def __init__(self):
        pass

    def is_shell(self, str_instruction):
        '''
        checks if instruction (as string) is a shell instruction

        :param str_instruction:
        :return:
        '''
        return str_instruction[0] == '$'

    def get_name_of_raw_instruction(self, str_instruction):
        '''
        gets instruction (as string) and returns the name (without $ sign)

        :param str_instruction:
        :return:
        '''
        tmp = str_instruction
        if self.is_shell(str_instruction):
            tmp = tmp[1:]
        return self.first_alphabetical_substring(tmp)

    def get_params_of_raw_instruction(self, str_instruction):
        '''
        gets instruction (as string) and returns a list of all parameters

        :param str_instruction:
        :return:
        '''
        idx_opening_parantheses = -1
        for i, c in enumerate(str_instruction):
            if c.isalnum() or c == '$':
                continue
            idx_opening_parantheses = i
            break
        idx_closing_parantheses = self.find_first_non_escaped_char(str_instruction, ')')
        str_params = str_instruction[idx_opening_parantheses + 1:idx_closing_parantheses]
        return self.parse_params(str_params)

    def get_return_of_raw_instruction(self, str_instruction):
        '''
        gets instruction (as string) and returns return value

        :param str_instruction:
        :return:
        '''
        str = str_instruction[self.find_last_non_escaped_char(str_instruction, '(') + 1:-1]
        return Variable(str, "OPEN", str.split(" "))

    def first_alphabetical_substring(self, string):
        '''
        finds the first occurring alphabetical substring of a string

        :param string:
        :return:
        '''
        result = []
        for c in string:
            if c.isalnum():
                result.append(c)
            else:
                break
        return "".join(result)

    def find_all_chars(self, string, char):
        '''
        returns a list of all indexes of a character in a string

        :param string:
        :param char:
        :return:
        '''
        return [i for i, c in enumerate(string) if c == char]

    def find_first_non_escaped_char(self, string, char):
        '''
        returns the index of the first non-escaped occurrence of char in a string
        calls the method find_first_match() to do the actual searching

        :param string:
        :param char:
        :return:
        '''
        idx = self.find_all_chars(string, char)
        return self.find_first_match(idx, string)

    def find_last_non_escaped_char(self, string, char):
        '''
        returns the index of the last non-escaped occurrence of char in a string
        calls the method find_first_match() to do the actual searching

        :param string:
        :param char:
        :return:
        '''
        idx = self.find_all_chars(string, char)
        reversed = idx[::-1]
        return self.find_first_match(reversed, string)

    def find_first_match(self, idxs, string):
        '''
        returns returns the first non-escaped occurrence of char in a string

        :param idxs:
        :param string:
        :return:
        '''
        for i in idxs:
            if i == 0:
                return 0
            if string[i - 1] == '\\':
                continue
            else:
                return i

    def escaped_split(self, s, delim):
        '''
        source: https://stackoverflow.com/questions/18092354/python-split-string-without-splitting-escaped-character

        splits a string by a delimiter ONLY if it is not escaped

        :param s:
        :param delim:
        :return:
        '''
        # split by escaped, then by not-escaped
        escaped_delim = '\\' + delim
        sections = [p.split(delim) for p in s.split(escaped_delim)]
        ret = []
        prev = None
        for parts in sections:  # for each list of "real" splits
            if prev is None:
                if len(parts) > 1:
                    # Add first item, unless it's also the last in its section
                    ret.append(parts[0])
            else:
                # Add the previous last item joined to the first item
                ret.append(escaped_delim.join([prev, parts[0]]))
            for part in parts[1:-1]:
                # Add all the items in the middle
                ret.append(part)
            prev = parts[-1]
        return ret

    def get_name_of_raw_rule(self, str_rule):
        '''
        gets a rule (as string) and returns the name of it

        :param str_rule:
        :return:
        '''
        idx_semicolon = str_rule.find(':')
        if self.is_shell(str_rule):
            return str_rule[1:idx_semicolon]
        else:
            return str_rule[:idx_semicolon]

    def get_params_of_raw_rule(self, str_rule):
        '''
        gets a rule (as string) and returns a list of all parameters (as Parameter instances)

        :param str_rule:
        :return:
        '''
        idx_semicolon = str_rule.find(':')
        idx_params_end = self.find_first_non_escaped_char(str_rule, ')')
        str_params = str_rule[idx_semicolon + 2:idx_params_end]
        return self.parse_params(str_params)

    def get_instructions_of_raw_rule(self, str_rule):
        '''
        gets a rule (as string) and returns a list of all instructions (as Instruction instances)

        :param str_rule:
        :return:
        '''
        if self.is_shell(str_rule):
            prefix = '$'
        else:
            prefix = ""
        name = self.get_name_of_raw_rule(str_rule)
        params = self.get_params_of_raw_rule(str_rule)
        length = len(prefix + name + ':' + '(' + ','.join(p.name for p in params) + ')')
        str_instr = str_rule[length + 1:self.find_last_non_escaped_char(str_rule, '(') - 1]
        if self.is_shell(str_rule):
            return str_instr
        else:
            return self.parse_instructions(str_instr)

    def parse_params(self, str_params):
        '''
        gets parameters (as string) and returns a list of Parameter instances

        :param str_params:
        :return:
        '''
        params_split = str_params.split(',')
        params = []
        for p in params_split:
            if len(p) == 0:
                # .split(',') on an empty string will return ['']
                # which should be ignored
                continue
            par = Variable(p, "", p.split(" "))
            if '*' in p and '+' in p:
                par.vtype = "OPEN+"
            elif '*' in p or len(p) > 1:
                par.vtype = "OPEN"
            else:
                par.vtype = "CLOSED"
            params.append(par)
        return params

    def parse_rules(self, str_rules):
        '''
        gets all rules (as string) and returns a list of Rule instances

        :param str_rules:
        :return:
        '''
        str_rules = self.escaped_split(str_rules, '.')
        str_rules = (rule.strip('\n') for rule in str_rules)
        rules = []
        for str_rule in str_rules:
            parsed = Rule(self.is_shell(str_rule), self.get_name_of_raw_rule(str_rule),
                          self.get_params_of_raw_rule(str_rule), self.get_instructions_of_raw_rule(str_rule),
                          self.get_return_of_raw_instruction(str_rule))
            rules.append(parsed)
        return rules

    def parse_instructions(self, str_instructions):
        '''
        gets instructions (as string) and returns a list of Instruction instances

        :param str_instructions: instructions of the form "INSTRUCTION1,INSTRUCTION2,..."
        :return: list of parsed instruction instances
        '''
        instructions_split = self.escaped_split(str_instructions, ';')
        instructions = []
        for str_instr in instructions_split:
            parsed = Instruction(self.is_shell(str_instr), self.get_name_of_raw_instruction(str_instr),
                                 self.get_params_of_raw_instruction(str_instr),
                                 self.get_return_of_raw_instruction(str_instr))
            instructions.append(parsed)
        return instructions


class Matcher:
    def __init__(self):
        pass

    @staticmethod
    def match_instruction_rule(instr, rule):
        '''
        checks if an instruction instance matches an rule instances by checking
        the name and number of parameters and if both are shell commands

        :param instr: instruction instance
        :param rule: rule instance
        :return: true if both instances have the same name and number of parameters and if both are shell commands
        '''
        return instr.is_shell == rule.is_shell and instr.name == rule.name and len(instr.body.params) == len(
            rule.body.params) and Matcher.check_param_types(instr.body.params, rule.body.params)

    @staticmethod
    def check_param_types(instr_params, rule_params):
        '''
        checks if the types of all parameters of a Instruction instance and a Rule instance match

        :param instr_params:
        :param rule_params:
        :return:
        '''
        for ip, rp in zip(instr_params, rule_params):
            if ip.vtype[0] != rp.vtype[0]:
                return False
        return True


class Executor:
    def __init__(self):
        pass

    def execute_shell_instruction(self, rule, instr):
        # TODO: alle parameter in shell command ersetzen und dann ausfuehren

        output = subprocess.check_output(rule.body.instructions, shell=True).decode('ascii')
        if rule.body.ret:
            instr.body.ret.value = output.split("\n")
        print(output)
