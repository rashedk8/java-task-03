# Java Execution Table - Complete State Tracking

## Execution Flow with State Changes

### **STEP 1: `u[0] = new User();`**

| Phase | Operation | user_count | u[0].captcha | u[0].user_add | Console Output |
|-------|-----------|-----------|--------------|---------------|----------------|
| Start | Create u[0] | 0 | - | - | - |
| Line 1 | `user_count++` | **1** | - | - | - |
| Line 2 | `captcha = "567" + "1"` | 1 | - | - | - |
| Line 3 | Print local captcha, this.captcha, user_count | 1 | **"567"** | null | **5671 567 1** |

---

### **STEP 2: `u[1] = new User(u[0]);`**

| Phase | Operation | user_count | u[0].captcha | u[1].captcha | u[1].user_add | Console Output |
|-------|-----------|-----------|--------------|--------------|---------------|----------------|
| Start | Call User(User user) with u[0] | 1 | "567" | - | - | - |
| Call `this()` | `user_count++` | **2** | "567" | - | - | - |
| In `this()` | `captcha = "567" + "2"` | 2 | "567" | - | - | - |
| In `this()` | Print in no-arg constructor | 2 | "567" | **"567"** | null | **5672 567 2** |
| Back to User(User) | `user_add = u[0]` | 2 | "567" | "567" | **→u[0]** | - |
| Back to User(User) | Print with user_add.captcha | 2 | "567" | "567" | →u[0] | **5672 567 2 567** |

---

### **STEP 3: `u[0].captcha = u[0].captcha + "CSE205";`**

| Phase | Operation | u[0].captcha | u[1].captcha | u[1].user_add.captcha | Console Output |
|-------|-----------|--------------|--------------|----------------------|----------------|
| Before | Read u[0].captcha | "567" | "567" | "567" (same object) | - |
| Execute | String concatenation | "567" + "CSE205" | "567" | "567" | - |
| After | Assign to u[0].captcha | **"567CSE205"** ✓ | "567" | **"567CSE205"** ✓ | - |

**Note:** `u[1].user_add` points to `u[0]`, so they share the same object!

---

### **STEP 4: `u[2] = new User(u[1].user_add.captcha);`**

| Phase | Operation | user_count | Parameter | u[2].captcha | Console Output |
|-------|-----------|-----------|-----------|--------------|----------------|
| Start | Resolve u[1].user_add.captcha | 2 | **"567CSE205"** | - | - |
| Call `this()` | `user_count++` | **3** | "567CSE205" | - | - |
| In `this()` | `captcha = "567" + "3"` | 3 | "567CSE205" | - | - |
| In `this()` | Print in no-arg constructor | 3 | "567CSE205" | **"567"** | **5673 567 3** |
| Back to User(String) | `this.captcha = "567CSE205"` | 3 | "567CSE205" | **"567CSE205"** ✓ | - |
| Back to User(String) | Print with this.captcha | 3 | "567CSE205" | "567CSE205" | **567CSE205 567CSE205 3** |

---

### **STEP 5: Final Print Statement**

```java
System.out.println(
    u[0].captcha + " " +    
    u[1].captcha + " " +    
    u[2].captcha            
);
```

| Variable | Value | Reason |
|----------|-------|--------|
| `u[0].captcha` | **"567CSE205"** | Modified in Step 3 |
| `u[1].captcha` | **"567"** | Never directly modified |
| `u[2].captcha` | **"567CSE205"** | Set from parameter in Step 4 |

**Output:** `567CSE205 567 567CSE205`

---

## Complete Console Output Sequence

```
5671 567 1
5672 567 2
5672 567 2 567
5673 567 3
567CSE205 567CSE205 3
567CSE205 567 567CSE205
```

---

## Object Reference Diagram

```
Initial State (After Step 2):
┌─────────────┐         ┌─────────────┐         ┌─────────────┐
│   u[0]      │         │   u[1]      │         │   u[2]      │
├─────────────┤         ├─────────────┤         ├─────────────┤
│ captcha:"567"│         │ captcha:"567"│        │   (null)    │
│ user_add:null│◄────────│ user_add:───┤│        │   (null)    │
└─────────────┘         └─────────────┘        └─────────────┘
                              ▲
                              │
                         Points to u[0]

After Step 3 (u[0].captcha modified):
┌──────────────────┐     ┌─────────────┐         ┌─────────────┐
│   u[0]           │     │   u[1]      │         │   u[2]      │
├──────────────────┤     ├─────────────┤         ├─────────────┤
│ captcha:"567..." │     │ captcha:"567"│        │   (null)    │
│      CSE205  ✓   │◄────│ user_add:───┤│        │   (null)    │
└──────────────────┘     └─────────────┘        └─────────────┘
       ▲                        ▲
       │                        │
  Modified              References u[0]

After Step 4 (u[2] created):
┌──────────────────┐     ┌─────────────┐         ┌──────────────────┐
│   u[0]           │     │   u[1]      │         │   u[2]           │
├──────────────────┤     ├─────────────┤         ├──────────────────┤
│ captcha:"567..." │     │ captcha:"567"│        │ captcha:"567CSE205"│
│      CSE205      │◄────│ user_add:───┤│        │ user_add: null   │
└──────────────────┘     └─────────────┘        └──────────────────┘
       ▲
       │
  Same value copied
```

---

## Memory State at Each Constructor Call

### Constructor 1: `User()` - No arguments
| Variable | Scope | Value | Persists? |
|----------|-------|-------|-----------|
| `user_count` | Static | 1 | ✅ Yes |
| `captcha` (local) | Local | "5671" | ❌ No |
| `this.captcha` | Instance | "567" | ✅ Yes |

### Constructor 2: `User(User user)` 
| Variable | Scope | Value | Persists? |
|----------|-------|-------|-----------|
| `user_count` | Static | 2 | ✅ Yes |
| `captcha` (local from this()) | Local | "5672" | ❌ No |
| `this.captcha` | Instance | "567" | ✅ Yes |
| `user_add` | Instance | Reference to u[0] | ✅ Yes |

### Constructor 3: `User(String captcha)`
| Variable | Scope | Value | Persists? |
|----------|-------|-------|-----------|
| `user_count` | Static | 3 | ✅ Yes |
| `captcha` (local from this()) | Local | "5673" | ❌ No |
| `captcha` (parameter) | Parameter | "567CSE205" | ❌ No |
| `this.captcha` | Instance | "567CSE205" | ✅ Yes |

---

## Key Takeaways

| Concept | Example from Code | Result |
|---------|------------------|--------|
| **Local vs Instance** | `String captcha` vs `this.captcha` | Local vars disappear after method; instance vars persist |
| **Object Reference** | `u[1].user_add = u[0]` | Both reference same object; changes visible through both |
| **Constructor Chaining** | `this()` calls no-arg first | parent constructor executes completely before child |
| **Static Variable** | `static int user_count` | Shared across ALL instances; increments persist |
| **Parameter Shadowing** | Constructor param `captcha` | Hides instance variable of same name in that scope |
