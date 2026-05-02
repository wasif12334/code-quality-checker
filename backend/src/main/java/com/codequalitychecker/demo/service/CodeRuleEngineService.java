package com.codequalitychecker.demo.service;

import com.codequalitychecker.demo.model.Issue;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CodeRuleEngineService {

    public List<Issue> analyze(String code) {

        List<Issue> issues = new ArrayList<>();

        if (code == null || code.isBlank()) {
            issues.add(new Issue("Error", "Code is empty", "High"));
            return issues;
        }

        // Rule 1: Long code
        if (code.length() > 300) {
            issues.add(new Issue(
                    "Code Smell",
                    "Code is too long. Consider splitting into smaller methods.",
                    "High"
            ));
        }

        // Rule 2: Poor variable naming
        if (code.contains("int a") || code.contains("int x")) {
            issues.add(new Issue(
                    "Naming Issue",
                    "Variable names are not meaningful.",
                    "Medium"
            ));
        }

        // Rule 3: System.out usage
        if (code.contains("System.out.println")) {
            issues.add(new Issue(
                    "Bad Practice",
                    "Avoid System.out.println. Use logging framework.",
                    "Low"
            ));
        }

        // Rule 4: Too many if conditions (basic check)
        int ifCount = code.split("if").length - 1;
        if (ifCount > 3) {
            issues.add(new Issue(
                    "Complexity",
                    "Too many if conditions. Consider simplifying logic.",
                    "Medium"
            ));
        }

        // Rule 5: No comments
        if (!code.contains("//") && !code.contains("/*")) {
            issues.add(new Issue(
                    "Readability",
                    "No comments found. Add comments for better understanding.",
                    "Low"
            ));
        }

        return issues;
    }
}