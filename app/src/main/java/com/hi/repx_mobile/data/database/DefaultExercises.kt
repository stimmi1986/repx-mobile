package com.hi.repx_mobile.data.database

import com.hi.repx_mobile.data.database.entities.Equipment
import com.hi.repx_mobile.data.database.entities.Exercise
import com.hi.repx_mobile.data.database.entities.MuscleGroups

/*
æfingar sem eru staðlaðar inn í gagnagrunn
svo notandinn hafi einhvcað til ap byrja með.
 */
object DefaultExercises {
    val exercises = listOf(
        // Chest
        Exercise(name = "Bench Press", primaryMuscle = MuscleGroups.CHEST, equipment = Equipment.BARBELL, description = "Flat barbell bench press"),
        Exercise(name = "Incline Bench Press", primaryMuscle = MuscleGroups.CHEST, equipment = Equipment.BARBELL, description = "Incline barbell bench press"),
        Exercise(name = "Dumbbell Bench Press", primaryMuscle = MuscleGroups.CHEST, equipment = Equipment.DUMBBELL, description = "Flat dumbbell bench press"),
        Exercise(name = "Incline Dumbbell Press", primaryMuscle = MuscleGroups.CHEST, equipment = Equipment.DUMBBELL, description = "Incline dumbbell press"),
        Exercise(name = "Cable Fly", primaryMuscle = MuscleGroups.CHEST, equipment = Equipment.CABLE, description = "Cable chest fly"),
        Exercise(name = "Push-Up", primaryMuscle = MuscleGroups.CHEST, equipment = Equipment.BODYWEIGHT, description = "Standard push-up"),
        Exercise(name = "Chest Dip", primaryMuscle = MuscleGroups.CHEST, equipment = Equipment.BODYWEIGHT, description = "Dip with forward lean for chest"),
        Exercise(name = "Pec Deck", primaryMuscle = MuscleGroups.CHEST, equipment = Equipment.MACHINE, description = "Machine pec deck fly"),

        // Back
        Exercise(name = "Deadlift", primaryMuscle = MuscleGroups.BACK, equipment = Equipment.BARBELL, description = "Conventional deadlift"),
        Exercise(name = "Barbell Row", primaryMuscle = MuscleGroups.BACK, equipment = Equipment.BARBELL, description = "Bent-over barbell row"),
        Exercise(name = "Pull-Up", primaryMuscle = MuscleGroups.BACK, equipment = Equipment.BODYWEIGHT, description = "Standard pull-up"),
        Exercise(name = "Lat Pulldown", primaryMuscle = MuscleGroups.BACK, equipment = Equipment.CABLE, description = "Cable lat pulldown"),
        Exercise(name = "Seated Cable Row", primaryMuscle = MuscleGroups.BACK, equipment = Equipment.CABLE, description = "Seated cable row"),
        Exercise(name = "Dumbbell Row", primaryMuscle = MuscleGroups.BACK, equipment = Equipment.DUMBBELL, description = "Single-arm dumbbell row"),
        Exercise(name = "T-Bar Row", primaryMuscle = MuscleGroups.BACK, equipment = Equipment.BARBELL, description = "T-bar row"),

        // Shoulders
        Exercise(name = "Overhead Press", primaryMuscle = MuscleGroups.SHOULDERS, equipment = Equipment.BARBELL, description = "Standing barbell overhead press"),
        Exercise(name = "Dumbbell Shoulder Press", primaryMuscle = MuscleGroups.SHOULDERS, equipment = Equipment.DUMBBELL, description = "Seated dumbbell shoulder press"),
        Exercise(name = "Lateral Raise", primaryMuscle = MuscleGroups.SHOULDERS, equipment = Equipment.DUMBBELL, description = "Dumbbell lateral raise"),
        Exercise(name = "Front Raise", primaryMuscle = MuscleGroups.SHOULDERS, equipment = Equipment.DUMBBELL, description = "Dumbbell front raise"),
        Exercise(name = "Face Pull", primaryMuscle = MuscleGroups.SHOULDERS, equipment = Equipment.CABLE, description = "Cable face pull for rear delts"),
        Exercise(name = "Reverse Pec Deck", primaryMuscle = MuscleGroups.SHOULDERS, equipment = Equipment.MACHINE, description = "Reverse pec deck for rear delts"),

        // Biceps
        Exercise(name = "Barbell Curl", primaryMuscle = MuscleGroups.BICEPS, equipment = Equipment.BARBELL, description = "Standing barbell curl"),
        Exercise(name = "Dumbbell Curl", primaryMuscle = MuscleGroups.BICEPS, equipment = Equipment.DUMBBELL, description = "Standing dumbbell curl"),
        Exercise(name = "Hammer Curl", primaryMuscle = MuscleGroups.BICEPS, equipment = Equipment.DUMBBELL, description = "Dumbbell hammer curl"),
        Exercise(name = "Preacher Curl", primaryMuscle = MuscleGroups.BICEPS, equipment = Equipment.BARBELL, description = "EZ-bar preacher curl"),
        Exercise(name = "Cable Curl", primaryMuscle = MuscleGroups.BICEPS, equipment = Equipment.CABLE, description = "Cable bicep curl"),

        // Triceps
        Exercise(name = "Tricep Pushdown", primaryMuscle = MuscleGroups.TRICEPS, equipment = Equipment.CABLE, description = "Cable tricep pushdown"),
        Exercise(name = "Close-Grip Bench Press", primaryMuscle = MuscleGroups.TRICEPS, equipment = Equipment.BARBELL, description = "Close-grip barbell bench press"),
        Exercise(name = "Skull Crusher", primaryMuscle = MuscleGroups.TRICEPS, equipment = Equipment.BARBELL, description = "Lying EZ-bar skull crusher"),
        Exercise(name = "Overhead Tricep Extension", primaryMuscle = MuscleGroups.TRICEPS, equipment = Equipment.DUMBBELL, description = "Dumbbell overhead tricep extension"),
        Exercise(name = "Tricep Dip", primaryMuscle = MuscleGroups.TRICEPS, equipment = Equipment.BODYWEIGHT, description = "Bodyweight tricep dip"),

        // Legs
        Exercise(name = "Squat", primaryMuscle = MuscleGroups.LEGS, equipment = Equipment.BARBELL, description = "Barbell back squat"),
        Exercise(name = "Front Squat", primaryMuscle = MuscleGroups.LEGS, equipment = Equipment.BARBELL, description = "Barbell front squat"),
        Exercise(name = "Leg Press", primaryMuscle = MuscleGroups.LEGS, equipment = Equipment.MACHINE, description = "Machine leg press"),
        Exercise(name = "Romanian Deadlift", primaryMuscle = MuscleGroups.LEGS, equipment = Equipment.BARBELL, description = "Romanian deadlift for hamstrings"),
        Exercise(name = "Leg Curl", primaryMuscle = MuscleGroups.LEGS, equipment = Equipment.MACHINE, description = "Machine leg curl"),
        Exercise(name = "Leg Extension", primaryMuscle = MuscleGroups.LEGS, equipment = Equipment.MACHINE, description = "Machine leg extension"),
        Exercise(name = "Calf Raise", primaryMuscle = MuscleGroups.LEGS, equipment = Equipment.MACHINE, description = "Standing calf raise"),
        Exercise(name = "Bulgarian Split Squat", primaryMuscle = MuscleGroups.LEGS, equipment = Equipment.DUMBBELL, description = "Bulgarian split squat"),
        Exercise(name = "Lunge", primaryMuscle = MuscleGroups.LEGS, equipment = Equipment.DUMBBELL, description = "Walking or stationary lunge"),
        Exercise(name = "Hip Thrust", primaryMuscle = MuscleGroups.LEGS, equipment = Equipment.BARBELL, description = "Barbell hip thrust for glutes"),

        // Core
        Exercise(name = "Plank", primaryMuscle = MuscleGroups.CORE, equipment = Equipment.BODYWEIGHT, description = "Standard front plank"),
        Exercise(name = "Crunch", primaryMuscle = MuscleGroups.CORE, equipment = Equipment.BODYWEIGHT, description = "Standard crunch"),
        Exercise(name = "Hanging Leg Raise", primaryMuscle = MuscleGroups.CORE, equipment = Equipment.BODYWEIGHT, description = "Hanging leg raise"),
        Exercise(name = "Cable Woodchop", primaryMuscle = MuscleGroups.CORE, equipment = Equipment.CABLE, description = "Cable woodchop for obliques"),
        Exercise(name = "Ab Rollout", primaryMuscle = MuscleGroups.CORE, equipment = Equipment.OTHER, description = "Ab wheel rollout"),
        Exercise(name = "Russian Twist", primaryMuscle = MuscleGroups.CORE, equipment = Equipment.BODYWEIGHT, description = "Seated Russian twist"),

        // Cardio
        Exercise(name = "Treadmill Running", primaryMuscle = MuscleGroups.CARDIO, equipment = Equipment.MACHINE, description = "Treadmill run"),
        Exercise(name = "Stationary Bike", primaryMuscle = MuscleGroups.CARDIO, equipment = Equipment.MACHINE, description = "Stationary bike cardio"),
        Exercise(name = "Rowing Machine", primaryMuscle = MuscleGroups.CARDIO, equipment = Equipment.MACHINE, description = "Rowing machine cardio"),
        Exercise(name = "Jump Rope", primaryMuscle = MuscleGroups.CARDIO, equipment = Equipment.OTHER, description = "Jump rope cardio"),

        // Full Body
        Exercise(name = "Clean and Press", primaryMuscle = MuscleGroups.FULL_BODY, equipment = Equipment.BARBELL, description = "Barbell clean and press"),
        Exercise(name = "Kettlebell Swing", primaryMuscle = MuscleGroups.FULL_BODY, equipment = Equipment.KETTLEBELL, description = "Two-handed kettlebell swing"),
        Exercise(name = "Turkish Get-Up", primaryMuscle = MuscleGroups.FULL_BODY, equipment = Equipment.KETTLEBELL, description = "Kettlebell Turkish get-up"),
        Exercise(name = "Thruster", primaryMuscle = MuscleGroups.FULL_BODY, equipment = Equipment.BARBELL, description = "Barbell thruster"),
    )
}
