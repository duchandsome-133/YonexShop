# Contributing to Yonex Shop

Thank you for your interest in contributing to the Yonex Sports E-Commerce platform!

## Code Contributions

1. **Fork the Repository**: Create your personal fork of the project on GitHub.
2. **Clone your fork**:
   ```bash
   git clone https://github.com/<your-username>/YonexShop.git
   cd YonexShop
   ```
3. **Create a branch**:
   ```bash
   git checkout -b feature/amazing-feature
   # or
   git checkout -b fix/issue-description
   ```
4. **Commit Guidelines**:
   Use Conventional Commits specification:
   - `feat:` A new feature
   - `fix:` A bug fix
   - `docs:` Documentation changes only
   - `style:` Formatting, missing semi colons, etc (no code change)
   - `refactor:` Refactoring production code
   - `test:` Adding missing tests, refactoring tests
   - `chore:` Updating build tasks, configs, etc.

5. **Test Your Changes**:
   Ensure the application builds and passes all checks:
   ```bash
   mvn clean test
   ```

6. **Submit a Pull Request**:
   Push to your fork and submit a PR to the `main` branch with a clear description of your changes.
