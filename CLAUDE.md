# Centurion Design Goals

## Core Constraint
- **Hundred Line Limit**: The interpreter must remain at exactly 100 lines or fewer
  - This is enforced by tests (see test/centurion/core_test.clj)
  - Every change must preserve or reduce line count

## Code Quality Principles

### No Code Golf
- Prioritize clarity over cleverness
- Use meaningful patterns and straightforward implementations
- The hundred-line constraint should be met through good design, not obfuscation

### Avoid Abbreviations
- Use full words in variable and function names
- Examples: `evaluate` not `eval`, `conjunction` not `conj`
- Exception: Standard Clojure idioms (e.g., `env` for environment, `f` for function parameters)

### Functional Purity
- No mutable state
- Functions should be pure transformations
- Use immutable data structures throughout
- Environment passing rather than global state

## Implementation Notes

- Pattern matching with core.match is preferred over explicit `cond` chains
- The `:seq` qualifier is necessary for correct variadic pattern matching
- Parser combinators provide elegant composition without additional dependencies
- Church encoding for lists maintains consistency with the functional approach
