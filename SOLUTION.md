# Java Constructor & Object Reference Problem - Final Solution

## Code Execution Trace

### Step 1: `u[0] = new User();`
**Constructor Called:** `User()` (No-arg)
```
user_count++ → user_count = 1
Local variable: captcha = "567" + "1" = "5671"
Instance variable: this.captcha = "567" (unchanged)
Print: 5671 567 1
```
**State:** 
- `u[0].captcha = "567"`
- `u[0].user_add = null`

---

### Step 2: `u[1] = new User(u[0]);`
**Constructor Called:** `User(User user)` 
- First calls `this()` → `User()`
  ```
  user_count++ → user_count = 2
  Local variable: captcha = "567" + "2" = "5672"
  Instance variable: this.captcha = "567"
  Print: 5672 567 2
  ```
- Then in `User(User user)`:
  ```
  user_add = u[0] (reference to u[0])
  Local captcha variable from parent = "5672"
  Print: 5672 567 2 567 (u[0].captcha is still "567")
  ```

**State:**
- `u[0].captcha = "567"`
- `u[1].captcha = "567"`
- `u[1].user_add = u[0]` (points to u[0])

---

### Step 3: `u[0].captcha = u[0].captcha + "CSE205";`
```
u[0].captcha = "567" + "CSE205" = "567CSE205"
```

**State:**
- `u[0].captcha = "567CSE205"` ✓ Modified!
- `u[1].captcha = "567"` (unchanged)
- `u[1].user_add.captcha = "567CSE205"` (refers to u[0])

---

### Step 4: `u[2] = new User(u[1].user_add.captcha);`
**Parameter passed:** `u[1].user_add.captcha = u[0].captcha = "567CSE205"`

**Constructor Called:** `User(String captcha)`
- First calls `this()` → `User()`
  ```
  user_count++ → user_count = 3
  Local variable: captcha = "567" + "3" = "5673"
  Instance variable: this.captcha = "567"
  Print: 5673 567 3
  ```
- Then in `User(String captcha)`:
  ```
  this.captcha = "567CSE205" (overwrites instance variable!)
  Parameter captcha = "567CSE205" (local variable)
  Print: 567CSE205 567CSE205 3
  ```

**State:**
- `u[2].captcha = "567CSE205"`
- `u[2].user_add = null`

---

### Step 5: Final Print Statement
```java
System.out.println(
    u[0].captcha + " " +    // "567CSE205"
    u[1].captcha + " " +    // "567"
    u[2].captcha            // "567CSE205"
);
```
**Output:** `567CSE205 567 567CSE205`

---

## Complete Console Output

```
5671 567 1
5672 567 2
5672 567 2 567
5673 567 3
567CSE205 567CSE205 3
567CSE205 567 567CSE205
```

---

## Key Concepts Explained

| Concept | Explanation |
|---------|-------------|
| **Constructor Chaining** | `this()` calls the no-arg constructor first, then executes the rest |
| **Local vs Instance Variable** | Local `String captcha` ≠ `this.captcha`. Local vars are temporary; instance vars persist |
| **Object Reference** | `u[1].user_add = u[0]` means they point to the SAME object. Modifying `u[0].captcha` affects `u[1].user_add.captcha` |
| **Static Variable** | `user_count` is shared across ALL instances; increments each time any User is created |
| **Parameter Shadowing** | Constructor parameter `captcha` shadows the instance variable name in the parameter scope |

---

## Answer Matrix

| Variable | Final Value | Reason |
|----------|-------------|--------|
| `u[0].captcha` | `"567CSE205"` | Modified in Step 3 |
| `u[1].captcha` | `"567"` | Never modified; points to u[0] via `user_add` but captcha field itself stays "567" |
| `u[2].captcha` | `"567CSE205"` | Set from u[0]'s value (via u[1].user_add.captcha) in constructor |
| `u[0].user_add` | `null` | Never assigned |
| `u[1].user_add` | Reference to `u[0]` | Assigned in User(User user) constructor |
| `u[2].user_add` | `null` | Never assigned |
| `User.user_count` | `3` | Static counter; incremented 3 times |

