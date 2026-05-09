import os
import re

BASE_DIR = "backend/src/main/java/edu/cit/yungco/expensemini/features"

# Find all java files
for root, dirs, files in os.walk(BASE_DIR):
    for f in files:
        if f.endswith(".java"):
            path = os.path.join(root, f)
            with open(path, "r") as file:
                content = file.read()
            
            # Need to add import for User if it references User but doesn't have it
            if " User " in content or "User>" in content or "(User " in content or "<User>" in content or "@ManyToOne" in content:
                if "import edu.cit.yungco.expensemini.features.user.User;" not in content and "features/user" not in path:
                    content = re.sub(r"(package [^;]+;)", r"\1\n\nimport edu.cit.yungco.expensemini.features.user.User;", content)
            
            if " Role " in content or "<Role>" in content:
                if "import edu.cit.yungco.expensemini.features.user.Role;" not in content and "features/user" not in path:
                    content = re.sub(r"(package [^;]+;)", r"\1\n\nimport edu.cit.yungco.expensemini.features.user.Role;", content)
                    
            if " Category " in content:
                if "import edu.cit.yungco.expensemini.features.expense.Category;" not in content and "features/expense" not in path:
                    content = re.sub(r"(package [^;]+;)", r"\1\n\nimport edu.cit.yungco.expensemini.features.expense.Category;", content)
                    
            if " EmailService " in content:
                if "import edu.cit.yungco.expensemini.features.notification.EmailService;" not in content and "features/notification" not in path:
                    content = re.sub(r"(package [^;]+;)", r"\1\n\nimport edu.cit.yungco.expensemini.features.notification.EmailService;", content)

            with open(path, "w") as file:
                file.write(content)

print("Fixed imports")
