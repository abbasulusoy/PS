from pslang_classes import *
from helper_classes import *
import sys


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
    variables = [
        {UUID: [value | UUID]},
        {UUID: [value | UUID]},
        ...
    ] 
    '''
    variables = []


    #format:
    #stack_pointer = [
    #    {from: rule_id, to: rule_id},
    #    {from: rule_id, to: rule_id},
    #    ...
    #]
    #stack_pointer = Stack()

    exec_queue = Queue()
    for i in initial_instructions:
        # put all initial instructions into the queue
        exec_queue.enqueue(i)
        # put all parameters into the variables list
        for p in i.body.params:
            variables.append({p.var_id: p.value})

    while not exec_queue.is_empty():
        instr = exec_queue.dequeue()
        for rule in rules:
            if not Matcher.match_instruction_rule(instr, rule):
                # if instruction and rule does not match, ignore
                continue

            # found rule to execute

            # set param values
            for rparam,iparam in zip(rule.body.params, instr.body.params):
                rparam.value = iparam.name
                entry = {
                    rparam.var_id: rparam.value
                }
                variables.append(entry)

            if rule.is_shell:
                executor.execute_shell_instruction(rule)
            else:
                # put all instructions into the queue
                for i in rule.body.instructions:
                    #stack_pointer.push({"from": instr.instr_id, "to": i.instr_id})
                    exec_queue.enqueue(i)



if __name__ == "__main__":
    run()
