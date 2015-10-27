__author__ = 'jw6dz, kq4hy'


# import collections
#
# rootVariables = collections.OrderedDict({})
# learnedVariables = collections.OrderedDict({})


rootVariables = {}
learnedVariables = {}
facts = []
rules = []
expr_vars = []
tree_leaves = []
operators = []
why_statements = []


class Node(object):
    def __init__(self):
        self.left = None
        self.right = None
        self.data = None
        self.head = None


def command(user_command):  # method to figure out which action (List, Learn, Why, Teach, or Query) to perform
    if isinstance(user_command, basestring):  # check if the user command is of type string
        if user_command.find(' ') == -1:
            if user_command == "List":
                list_info()
            elif user_command == "Learn":
                learn()
        else:
            index_of_space = user_command.find(' ')
            action = user_command[: index_of_space]
            expression = user_command[index_of_space + 1:]
            if action == "Why":
                why(expression)
            elif action == "Teach":
                teach(expression)
            elif action == "Query":
                query(expression)


def list_info():
    print("Root Variables:")
    for var in rootVariables:
        print "\t" + var + " = " + rootVariables[var][0]

    print("Learned Variables:")
    for var in learnedVariables:
        print "\t" + var + " = " + learnedVariables[var][0]

    print("Facts:")
    for var in facts:
        print "\t" + var

    print("Rules:")
    for var in rules:
        print "\t" + var


def teach(expression):
    equals_index = expression.find("=")  # determines the location of the "="
    if equals_index != -1:  # if the = exists
        variable_name = expression[3:equals_index-1]
        after_equals = expression[equals_index+2:]
        if expression[0:2] == "-R":  # -R indicates root variable
            rootVariables[variable_name] = [after_equals, False, False]
        elif expression[0:2] == "-L":  # -L indicates learned variable
            learnedVariables[variable_name] = [after_equals, False, False]
        else:  # teaching a fact
            variable_name = expression[:equals_index-1]
            if rootVariables.get(variable_name) is not None:  # check if variable exists in rootVariables
                if after_equals == "true":
                    rootVariables[variable_name] = [rootVariables[variable_name][0], True, True]
                    facts.append(variable_name)
                else:
                    rootVariables[variable_name] = [rootVariables[variable_name][0], False, False]
                    facts.remove(variable_name)
                for learned_var in learnedVariables:  # set all learned variables back to false
                    if learned_var in facts:
                        facts.remove(learned_var)
                    learnedVariables[learned_var] = [learnedVariables[learned_var][0], False, False]
            else:  # variable is not a root variable
                print("That root variable does not exist.")
                return
    else:  # = does not exist, should be a rule
        rule_split = expression.split(" -> ")
        expr_vars[:] = []  # clear temp_variables list
        if learnedVariables.get(rule_split[1]) is None:  # invalid rule format
            return
        create_expr_tree(rule_split[0], "teach")  # create the expression tree associated with the rule
        for var in expr_vars:
            if rootVariables.get(var) is not None or learnedVariables.get(var) is not None:
                continue
            else:
                return
        rules.append(expression)


def learn():
    for rule in rules:
        rule_split = rule.split(" -> ")
        learned_var = rule_split[1]
        root_node = create_expr_tree(rule_split[0], "learn")
        expr_result = eval_expr_tree(root_node, "learn")
        if expr_result != learnedVariables[learned_var][1]:
            learnedVariables[learned_var] = [learnedVariables[learned_var][0], expr_result, expr_result]
            if expr_result:
                facts.append(learned_var)
            else:
                if learned_var in facts:
                    facts.remove(learned_var)
            learn()


def query(message):
    root_node = create_expr_tree(message, "query")  # root_node is the tree with Boolean values and operators
    if eval_expr_tree(root_node, "query"):  # evaluate the Booleans inside the tree
        print "true"
    else:
        print "false"


def why(message):
    why_statements[:] = []
    root = create_expr_tree(message, "why")
    if evalTreeWhy(root):
        print "I THUS KNOW THAT " + message
    else:
        print "THUS I CANNOT PROVE " + message


def create_expr_tree(message, action):  # creates a tree based on the message and action passed in
    if '|' not in message and '&' not in message and '!' not in message:
        leaf_node = Node()
        if action == "learn":  # tree will consist of variables and operators
            leaf_node.data = message
            if action == "teach":
                expr_vars.append(message)
        elif action == "query":  # tree will consist of booleans and operators
            leaf_node.data = query_for_fact(message)
        elif action == "why":
            # add in some logic here about what the tree should look like for why
            print "We need to do something here"
        return leaf_node

    message = remove_paran(message)
    root_char = ""
    ord_root_val = root_index = index = counter = 0
    for character in message:
        if character == '(':
            counter += 1
        elif character == ')':
            counter -= 1
        if counter == 0:
            # keep checking each letter until reaching the operator of least importance
            if not ((64 < ord(character) < 91) or (140 < ord(character) < 173)):  # if letter is not a letter
                if ord(character) > ord_root_val:
                    root_char = character
                    ord_root_val = ord(root_char)
                    root_index = index
        index += 1

    root = Node()
    root.data = root_char
    if root_char == "!":
        root.left = create_expr_tree(message[root_index + 1:], action)
        root.left.head = root
    else:
        root.left = create_expr_tree(message[0:root_index], action)
        root.left.head = root
        root.right = create_expr_tree(message[root_index + 1:], action)
        root.right.head = root
    return root


def remove_paran(expr):  # remove extra parentheses
    while expr[0] == '(' and expr[len(expr) - 1] == ')':
        expr = expr[1:len(expr)-1]
    return expr


def eval_expr_tree(root, action):  # returns True or False based on the evaluation of the expression tree
    if action == "query" and root.data is True or root.data is False:
        return root.data
    elif action == "learn" and '|' not in root.data and '&' not in root.data and '!' not in root.data:
        if rootVariables.get(root.data) is not None:
            return rootVariables[root.data][1]
        else:
            return learnedVariables[root.data][1]
    else:
        if root.data == '|':
            return eval_expr_tree(root.left, action) or eval_expr_tree(root.right, action)
        elif root.data == '&':
            return eval_expr_tree(root.left, action) and eval_expr_tree(root.right, action)
        else:
            return not eval_expr_tree(root.left, action)


def query_for_fact(var):
    if var in facts:
        return True
    else:
        var_rules = get_rules(var)
        if len(var_rules) == 0:  # no rules associated with this variable and not in facts so return False
            return False
        for expr in var_rules:  # for every expression associated with that variable, backwards chain it
            root = create_expr_tree(expr, "query")  # create a tree for that expression
            if not eval_expr_tree(root, "query"):  # evaluate that tree
                return False
            learnedVariables[var] = [learnedVariables[var][0], learnedVariables[var][1], eval_expr_tree(root, "query")]
        return True  # all expressions returned true


def evalTreeWhy(root):
    #if '|' not in root.data and '&' not in root.data and '!' not in root.data:
        #check if its a root variable, if it is, print out ___ 
        #if not a root variable, whyforfact it
    if is_parent(root):
        if root.data == '!':
            if rootVariables.get(root.left.data) is not None and rootVariables[root.left.data][1]:
                print "I KNOW THAT " + rootVariables[root.left.data][0]
                return rootVariables[root.left.data][1]
            elif rootVariables.get(root.left.data) is not None and not rootVariables[root.left.data][1]:
                print "I KNOW IT IS NOT TRUE THAT " + rootVariables[root.left.data][0]
                return rootVariables[root.left.data][1]
                
        else:
            if rootVariables.get(root.left.data) is not None:
                varleft_info = rootVariables[root.left.data]
                print "I KNOW THAT " + varleft_info[0]
            if rootVariables.get(root.right.data) is not None:
                varright_info = rootVariables[root.right.data]
                print "I KNOW THAT " + varright_info[0]
            if learnedVariables.get(root.left.data) is not None:
                varleft_info = learnedVariables[root.left.data]
                print "BECAUSE "
            if learnedVariables.get(root.right.data) is not None:
                varright_info = learnedVariables[root.right.data]
                print "BECAUSE "

            if root.data == '&':
                print "BECAUSE " + varleft_info + " AND " + varright_info
                return query_for_fact(root.left.data) and query_for_fact(root.right.data)
    else:
        if root.data == '|':
            return evalTreeWhy(root.left) or evalTreeWhy(root.right)
        elif root.data == '&':
            return evalTreeWhy(root.left) and evalTreeWhy(root.right)
        else:
            return not evalTreeWhy(root.left)


def whyForFact(var):
    if var.data in facts:
        return True
    else:
        results = get_rules(var.data)
        if len(results) == 0:
            return False
        for res in results:
            root = create_expr_tree(res)
            left_side = evalTreeWhy(root)
            if not left_side:
                why_statements.append("BECAUSE IT IS NOT TRUE THAT " )
                return False
            learnedVariables[var] = [learnedVariables[var][0], learnedVariables[var][1], left_side]
        return True


def get_rules(var):  # returns a list of expressions of rules with var as their consequence
    results = []
    for rule in rules:
        chunks = rule.split(" -> ")
        if chunks[1] == var:
            results.append(chunks[0])
    return results


def get_leaves(root_node):  # returns the leaves associated with a root node
    if root_node.left is None and root_node.right is None:
        tree_leaves.append(root_node.data)
    elif root_node.left is None:
        get_leaves(root_node.right)
    elif root_node.right is None:
        get_leaves(root_node.left)
    else:
        get_leaves(root_node.left)
        get_leaves(root_node.right)


def is_parent(node):  # returns true if node is parent of one leaf or two leaves
    if node.data == '!':
        return node.left.left is None
    return node.left.left is None and node.right.left is None


def main():
    rootVariables['A'] = ["s", True, True]
    facts.append('A')
    learnedVariables['B'] = ["b", False, False]
    learnedVariables['C'] = ["c", False, False]
    rules.append("A -> B")

    while True:
        message = raw_input('>')
        if message == "exit" or message == "quit" or message == "e":
            break
        command(message)


if __name__ == "__main__":
    main()