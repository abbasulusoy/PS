from pslang_classes import *
from helper_classes import *
import sys
import re


def run():
    '''
    PARSING STARTS HERE

    1. read arguments: program (set of rules) and entry point (initial instructions)
    2. parse rules and initial instructions and put into queue
    3. for each instruction find corresponding rule to execute: parse head, body and return values of rule
    4. execute parsed rule and next instructions into queue
    '''
    if len(sys.argv) != 3:
        print("Usage:\npython main.py <FILE-CONTAINING-PROGRAM> <FILE-CONTAINING-FIRST-GOAL(S)>")
        exit(1)
    with open(sys.argv[1], 'r') as file_program:
        str_program = file_program.read()
    with open(sys.argv[2], 'r') as file_initial_instructions:
        str_initial_instructions = file_initial_instructions.read()
    parser = Parser()
    executor = Executor()
    rules = parser.parse_rules(str_program)
    initial_instructions = parser.parse_instructions(str_initial_instructions)

    '''
    format:
    variables = {
        UUID: [value | UUID],
        UUID: [value | UUID],
        ...
    }
    '''
    variables = {}

    exec_queue = Queue()
    for ri in initial_instructions:
        # put all initial instructions into the queue
        exec_queue.enqueue(ri)
        # put all parameters into the variables list
        # will be put into dict later, why here?
        for iparam in ri.body.params:
            variables[iparam.var_id] = Variable(iparam.name, iparam.vtype, iparam.value)

    while not exec_queue.is_empty():
        instr = exec_queue.dequeue()
        for rule in rules:
            if not Matcher.match_instruction_rule(instr, rule):
                # if instruction and rule does not match, ignore
                continue

            # found rule to execute

            # set param values
            for rparam, iparam in zip(rule.body.params, instr.body.params):
                if rparam.vtype == "OPEN+":
                    var_id = getId(iparam, variables)
                    value = variables[var_id].value

                    var = Variable(rparam.name, "OPEN+", value)
                    variables[rparam.var_id] = var

                    var = Variable(rparam.name.split("*")[0], "CLOSED", [value[0]])
                    variables[rparam.var_id+"-0"] = var

                    var = Variable("*"+rparam.name.split("*")[1], "OPEN", value[1:])
                    variables[rparam.var_id+"-1"] = var
                elif rparam.vtype == "OPEN":
                    if iparam.var_id+"-1" in variables:
                        var = Variable(rparam.name, "OPEN", variables[iparam.var_id + "-1"].value)
                        variables[rparam.var_id+"-1"] = var
                    else:
                        var = Variable(rparam.name, "OPEN", variables[iparam.var_id].value)
                        variables[rparam.var_id] = var
                else:
                    if iparam.var_id+"-0" in variables:
                        var = Variable(rparam.name, "CLOSED", variables[iparam.var_id+"-0"].value)
                        variables[rparam.var_id+"-0"] = var
                    else:
                        var = Variable(rparam.name, "CLOSED", variables[iparam.var_id].value)
                        variables[rparam.var_id] = var

            if rule.is_shell:
                for param in rule.body.params:
                    id = getId(param, variables)
                    var = variables[id]
                    tmp = {var.name: var.value}
                    if param.vtype == "OPEN+":
                        tmp = {
                            var.name.split("*")[0]: [var.value[0]],
                            "*" + var.name.split("*")[1]: var.value[1:]
                        }
                    for name in tmp:
                        rule.body.instructions = re.sub(
                            r"(?<!\\)" + name.replace("+", "\\+").replace("*", "\\*") + r"(?!\w)",
                            " ".join(tmp[name]), rule.body.instructions)

                executor.execute_shell_instruction(rule, instr)
                if instr.body.ret:
                    variables[instr.body.ret.var_id] = instr.body.ret
            else:
                # set return value to param values if needed
                for instr1 in rule.body.instructions:
                    if not instr1.body.ret:
                        continue
                    for instr2 in rule.body.instructions:
                        if instr1 == instr2:
                            continue
                        for p in instr2.body.params:
                            if p.name == instr1.body.ret.name:
                                variables[p.var_id] = instr1.body.ret
                                break

                # put all instructions into the queue
                for ri in rule.body.instructions:
                    if ri.body.params:
                        for iparam in ri.body.params:
                            for rparam in rule.body.params:
                                var_id = getId(rparam, variables)
                                var = Variable(variables[var_id].name, "", [])
                                if iparam.name == rparam.name:
                                    var.vtype = iparam.vtype
                                    id = getId(rparam, variables)
                                    var.value = variables[id].value
                                    variables[iparam.var_id] = var
                                elif rparam.vtype == "OPEN+":
                                    plus = rparam.name.split("*")[0]
                                    star = "*"+rparam.name.split("*")[1]
                                    if plus == iparam.name:
                                        var.vtype = "CLOSED"
                                        var.value = [variables[var_id].value[0]]
                                        variables[iparam.var_id+"-0"] = var
                                    elif star == iparam.name:
                                        var.vtype = "OPEN"
                                        var.value = variables[var_id].value[1:]
                                        variables[iparam.var_id+"-1"] = var
                    exec_queue.enqueue(ri)

                # if rule has no instructions but param and return, then match variables
                if not rule.body.instructions:
                    if rule.body.params and rule.body.ret:
                        for rpar, ipar in zip(rule.body.params, instr.body.params):
                            if rpar.vtype == "OPEN+":
                                star = "*" + rpar.name.split("*")[1]
                                # return values have to be OPEN per definition
                                if star == rule.body.ret.name:
                                    instr.body.ret.value = variables[instr.body.params[0].var_id].value[1:]
                                    variables[instr.body.ret.var_id] = instr.body.ret
                            else:
                                if rpar.name == rule.body.ret.name:
                                    variables[instr.body.ret.var_id] = instr.body.ret

            break


def getId(id, variables):
    '''
    check if id is in variables, if not then check for id-0 and id-1
    :param id:
    :param variables:
    :return:
    '''
    if id.vtype == "OPEN":
        if id.var_id + "-1" in variables:
            return id.var_id + "-1"
        else:
            return id.var_id
    elif id.vtype == "CLOSED":
        if id.var_id + "-0" in variables:
            return id.var_id + "-0"
        else:
            return id.var_id
    else:
        return id.var_id


if __name__ == "__main__":
    run()
