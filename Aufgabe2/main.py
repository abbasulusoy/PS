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
        for ip in ri.body.params:
            print("a " + ip.var_id + "   " + ip.name + " " + str(ip.value))
            variables[ip.var_id] = ip

    while not exec_queue.is_empty():
        instr = exec_queue.dequeue()
        for rule in rules:
            if not Matcher.match_instruction_rule(instr, rule):
                # if instruction and rule does not match, ignore
                continue
            # found rule to executeparse_instructions


            #print("****")
            #for s in variables:
            #    print(variables[s])


            # set param values
            for rparam, iparam in zip(rule.body.params, instr.body.params):
                if rparam.vtype == "OPEN+":
                    tmpValue = variables[iparam.var_id].value

                    variables[rparam.var_id] = variables[iparam.var_id]
                    variables[rparam.var_id].name = rparam.name
                    variables[rparam.var_id].value = tmpValue
                    print("b " + rparam.var_id + "   " + variables[rparam.var_id].name + "  " + str(variables[rparam.var_id].value))

                    #variables[iparam.var_id].name = rparam.name.split("*")[0]
                    variables[rparam.var_id+"-0"] = variables[iparam.var_id]
                    variables[rparam.var_id+"-0"].name = rparam.name.split("*")[0]
                    variables[rparam.var_id+"-0"].value = [tmpValue[0]]
                    print("c " + rparam.var_id+"-0" + "   " + variables[rparam.var_id+"-0"].name + "  " + str(variables[rparam.var_id+"-0"].value))

                    variables[rparam.var_id+"-1"] = variables[iparam.var_id]
                    variables[rparam.var_id+"-1"].name = "*"+rparam.name.split("*")[1]
                    variables[rparam.var_id+"-1"].value = tmpValue[1:]
                    print("d " + rparam.var_id+"-1" + "   " + variables[rparam.var_id+"-1"].name + "  " + str(variables[rparam.var_id+"-1"].value))
                else:
                    variables[rparam.var_id] = variables[iparam.var_id]
                    variables[rparam.var_id].name = rparam.name
                    print("e " + rparam.var_id + "   " + variables[rparam.var_id].name+"  "+str(variables[rparam.var_id].value))

            # TODO: 1.3 + rekursive variablen werte finden und ersetzen und Eintrag in variables bearbeiten
            # for x in variables:
            #    print(x)
            #    print(variables[x].name +": "+variables[x].value)
            if rule.is_shell:
                #print(rule.body.instructions)
                for param in rule.body.params:
                    rule.body.instructions = re.sub(
                        r"(?<!\\)" + variables[param.var_id].name.replace("+", "\+").replace("*", "\*") + r"(?!\w)",
                        " ".join(variables[param.var_id].value), rule.body.instructions)
                executor.execute_shell_instruction(rule, instr)
                # TODO: return wert in variable-liste ersetzen
                if instr.body.ret:
                    print("f " + rule.body.ret.var_id + "   " + instr.body.ret.name + "  " +str(instr.body.ret.value))
                    variables[rule.body.ret.var_id] = instr.body.ret
            else:

                # put all instructions into the queue
                for ri in rule.body.instructions:
                    #print(i.is_shell)
                    # TODO: 2.1 Parameter und return Variablen in variables-liste schreiben
                    if ri.body.ret:
                        variables[rule.body.ret.var_id] = ri.body.ret
                    if ri.body.params:
                        for ip in ri.body.params:
                            for rp in rule.body.params:
                                if ip.name == rp.name:
                                    variables[ip.var_id] = variables[rp.var_id]
                                    print(ip.name + " " + str(rp.value) + str(variables[rp.var_id].value))
                                    print("i " + ip.var_id + "   " + variables[ip.var_id].name + "  " + str(variables[ip.var_id].value))
                                elif rp.vtype == "OPEN+":
                                    plus = rp.name.split("*")[0]
                                    star = "*"+rp.name.split("*")[1]
                                    #print(ip.var_id+"-0"+"     "+rp.var_id)
                                    if plus == ip.name:
                                        variables[ip.var_id+"-0"] = variables[rp.var_id]
                                        print("g " + ip.var_id+"-0" + "   " + variables[ip.var_id+"-0"].name + "  " + str(variables[ip.var_id+"-0"].value))
                                    elif star == ip.name:
                                        variables[ip.var_id+"-1"] = variables[rp.var_id]
                                        print("h " + ip.var_id + "-1" + "   " + variables[ip.var_id+"-1"].name + "  " + str(variables[ip.var_id+"-1"].value))

                    exec_queue.enqueue(ri)
            print("END OF FOR")

if __name__ == "__main__":
    run()
